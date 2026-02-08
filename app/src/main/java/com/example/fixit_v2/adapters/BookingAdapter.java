package com.example.fixit_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.activities.CreateReviewActivity;
import com.example.fixit_v2.databinding.ItemBookingCardBinding;
import com.example.fixit_v2.models.Order;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private static final String TAG = "BookingAdapter";
    private final Context context;
    private final List<Order> orderList;
    private final ServiceDataSource serviceDataSource;
    private final TechnicianDataSource technicianDataSource;
    private final SimpleDateFormat uiDateFormat = new SimpleDateFormat("\'On\' dd MMM yyyy, HH:mm", Locale.getDefault());

    public BookingAdapter(Context context, List<Order> orderList, ServiceDataSource sds, TechnicianDataSource tds) {
        this.context = context;
        this.orderList = orderList;
        this.serviceDataSource = sds;
        this.technicianDataSource = tds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemBookingCardBinding binding = ItemBookingCardBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(orderList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // Helper method to parse multiple date formats
    private Date parseDateString(String dateString) {
        if (dateString == null) return null;
        // List of possible date formats from database
        String[] formats = {
                "yyyy-MM-dd HH:mm:ss", // Original assumption
                "dd-MM-yy",            // Format from the crash log
                "yyyy-MM-dd",
                "dd-MM-yyyy"
        };
        for (String format : formats) {
            try {
                return new SimpleDateFormat(format, Locale.getDefault()).parse(dateString);
            } catch (ParseException e) {
                // Ignore and try the next format
            }
        }
        Log.e(TAG, "Could not parse date: " + dateString + ". All formats failed.");
        return null; // Return null if all formats fail
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookingCardBinding binding;

        public ViewHolder(ItemBookingCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                Order order = orderList.get(position);
                String status = order.getStatus().toLowerCase();

                if (status.contains("completed") || status.contains("selesai")) {
                    Intent intent = new Intent(context, CreateReviewActivity.class);
                    intent.putExtra("SERVICE_ID", order.getServiceId());
                    SharedPreferences preferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
                    int userId = preferences.getInt("user_id", -1);
                    intent.putExtra("USER_ID", userId);
                    context.startActivity(intent);
                } else {
                    Service service = serviceDataSource.getServiceById(order.getServiceId());
                    if (service != null) {
                        Technician technician = technicianDataSource.getTechnicianById(service.getTechnicianId());
                        if (technician != null && technician.getPhoneNumber() != null && !technician.getPhoneNumber().isEmpty()) {
                            String phoneNumber = technician.getPhoneNumber();
                            if (!phoneNumber.startsWith("62")) {
                                phoneNumber = "62" + phoneNumber.replaceFirst("^0", "");
                            }
                            phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");

                            String message = "Halo, saya ingin bertanya tentang pesanan layanan '" + service.getServiceName() + "' saya.";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message)));
                            try {
                                context.startActivity(intent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(context, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Technician contact not available.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        void bind(Order order) {
            Service service = serviceDataSource.getServiceById(order.getServiceId());
            if (service != null) {
                binding.textViewServiceName.setText(service.getServiceName());
                Technician technician = technicianDataSource.getTechnicianById(service.getTechnicianId());
                if (technician != null) {
                    binding.textViewProviderName.setText("by " + technician.getName());
                }
            }
            
            // Use the new robust date parsing method
            Date date = parseDateString(order.getOrderDate());
            if (date != null) {
                binding.textViewBookingDate.setText(uiDateFormat.format(date));
            } else {
                binding.textViewBookingDate.setText(order.getOrderDate() != null ? order.getOrderDate() : "No date");
            }
            
            setStatus(order.getStatus());
        }

        private void setStatus(String status) {
            binding.textViewStatus.setText(status);
            Drawable background;
            int textColor;

            String lowerStatus = status.toLowerCase();
            if (lowerStatus.contains("completed") || lowerStatus.contains("selesai")) {
                background = ContextCompat.getDrawable(context, R.drawable.badge_background_completed);
                textColor = ContextCompat.getColor(context, R.color.black);
                binding.textViewProviderName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_review, 0);
            } else {
                background = lowerStatus.contains("pending") ? ContextCompat.getDrawable(context, R.drawable.badge_background_pending) : ContextCompat.getDrawable(context, R.drawable.badge_background_canceled);
                textColor = lowerStatus.contains("pending") ? ContextCompat.getColor(context, R.color.black) : ContextCompat.getColor(context, R.color.white);
                binding.textViewProviderName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_whatsapp, 0);
            }
            binding.textViewStatus.setBackground(background);
            binding.textViewStatus.setTextColor(textColor);
        }
    }
}
