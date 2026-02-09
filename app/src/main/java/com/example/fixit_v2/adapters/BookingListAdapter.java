package com.example.fixit_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.activities.CreateReviewActivity;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Order;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;

import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.BookingViewHolder> {

    private Context context;
    private List<Order> orders;
    private TechnicianDataSource technicianDataSource;
    private ServiceDataSource serviceDataSource;
    private int userId;

    public BookingListAdapter(Context context, List<Order> orders, TechnicianDataSource techDS, ServiceDataSource serviceDS, int userId) {
        this.context = context;
        this.orders = orders;
        this.technicianDataSource = techDS;
        this.serviceDataSource = serviceDS;
        this.userId = userId;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_booking_item, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameView, technicianNameView, statusView;
        Button buttonMessage;
        ImageView serviceIconView;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceNameView = itemView.findViewById(R.id.textViewServiceName);
            technicianNameView = itemView.findViewById(R.id.textViewTechnicianName);
            statusView = itemView.findViewById(R.id.textViewStatus);
            buttonMessage = itemView.findViewById(R.id.buttonMessage);
            serviceIconView = itemView.findViewById(R.id.imageViewServiceIcon);
        }

        void bind(final Order order) {
            statusView.setText("Status: " + order.getStatus());

            Service service = serviceDataSource.getServiceById(order.getServiceId());
            if (service != null) {
                serviceNameView.setText(service.getServiceName());
                Technician technician = technicianDataSource.getTechnicianById(service.getTechnicianId());
                
                if (technician != null) {
                    technicianNameView.setText(technician.getName());
                    buttonMessage.setVisibility(View.VISIBLE);
                    buttonMessage.setOnClickListener(v -> {
                        String phoneNumber = technician.getPhoneNumber();
                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
                            String url = "https://wa.me/" + phoneNumber.replaceAll("[^\\d]", "");
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Technician phone number not available.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    technicianNameView.setText("Technician Not Found");
                    buttonMessage.setVisibility(View.GONE);
                }
            } else {
                serviceNameView.setText("Service Not Found");
                technicianNameView.setText("");
                buttonMessage.setVisibility(View.GONE);
            }

            // Handle click on the whole item for review
            itemView.setOnClickListener(v -> {
                if ("Selesai".equals(order.getStatus())) {
                    Intent intent = new Intent(context, CreateReviewActivity.class);
                    intent.putExtra("SERVICE_ID", order.getServiceId());
                    intent.putExtra("USER_ID", userId);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "You can only review completed orders.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
