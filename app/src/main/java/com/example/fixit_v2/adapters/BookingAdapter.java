package com.example.fixit_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
    private final SimpleDateFormat uiDateFormat = new SimpleDateFormat("'On' dd MMM yyyy, HH:mm", Locale.getDefault());

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

    private Date parseDateString(String dateString) {
        if (dateString == null) return null;
        String[] formats = {"yyyy-MM-dd HH:mm:ss", "dd-MM-yy", "yyyy-MM-dd", "dd-MM-yyyy"};
        for (String format : formats) {
            try {
                return new SimpleDateFormat(format, Locale.getDefault()).parse(dateString);
            } catch (ParseException e) {
                // Continue
            }
        }
        Log.e(TAG, "Could not parse date: " + dateString);
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookingCardBinding binding;

        public ViewHolder(ItemBookingCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Order order) {
            if (order == null) return;

            Service service = serviceDataSource.getServiceById(order.getServiceId());
            if (service != null) {
                binding.textViewServiceName.setText(service.getServiceName());
                Technician technician = technicianDataSource.getTechnicianById(service.getTechnicianId());
                if (technician != null) {
                    binding.textViewProviderName.setText("by " + technician.getName());
                    setupActions(order, technician, service);
                }
            }

            Date date = parseDateString(order.getOrderDate());
            if (date != null) {
                binding.textViewBookingDate.setText(uiDateFormat.format(date));
            } else {
                binding.textViewBookingDate.setText(order.getOrderDate() != null ? order.getOrderDate() : "No date");
            }

            setStatus(order.getStatus());
        }

        private void setupActions(final Order order, final Technician technician, final Service service) {
            // 1. Reset semua listener dan visibilitas ke kondisi awal untuk menghindari bug di RecyclerView
            itemView.setOnClickListener(null);
            itemView.setClickable(false);
            binding.buttonAction.setOnClickListener(null);
            binding.buttonAction.setVisibility(View.GONE); // Sembunyikan tombol secara default

            if (order.getStatus() == null) {
                // Jika status null, tidak ada aksi yang perlu dilakukan.
                return;
            }

            String status = order.getStatus().toLowerCase();

            // 2. Logika Utama: Tentukan aksi berdasarkan status
            // KONDISI A: Jika status sudah "success", "completed", atau "selesai"
            if (status.contains("success") || status.contains("completed") || status.contains("selesai")) {
                // AKSI: Membuat seluruh kartu dapat diklik untuk memberi ulasan. Tombol aksi tetap tersembunyi.
                itemView.setClickable(true);
                itemView.setFocusable(true); // Meningkatkan aksesibilitas
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, CreateReviewActivity.class);
                    // Mengirim data yang diperlukan ke halaman review
                    intent.putExtra("SERVICE_ID", order.getServiceId());

                    // Mengambil User ID dari SharedPreferences untuk dikirim
                    SharedPreferences preferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
                    int userId = preferences.getInt("user_id", -1); // -1 sebagai default jika tidak ditemukan
                    if (userId == -1) {
                        Toast.makeText(context, "Sesi pengguna tidak valid. Silakan login kembali.", Toast.LENGTH_LONG).show();
                        return; // Hentikan aksi jika user_id tidak ada
                    }
                    intent.putExtra("USER_ID", userId);

                    context.startActivity(intent);
                });

            }
            // KONDISI B: Jika status BUKAN "canceled" (dan juga bukan success/completed/selesai dari blok if di atas)
            else if (!status.contains("canceled")) {
                // AKSI: Menampilkan tombol WhatsApp untuk menghubungi teknisi.
                // Ini akan berlaku untuk status seperti "pending", "in progress", "diproses", dll.
                binding.buttonAction.setVisibility(View.VISIBLE);
                binding.buttonAction.setImageResource(R.drawable.ic_whatsapp); // Pastikan drawable ini ada di res/drawable

                binding.buttonAction.setOnClickListener(v -> {
                    String phoneNumber = technician.getPhoneNumber();
                    if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                        String internationalFormatNumber = phoneNumber;
                        // Mengonversi nomor lokal (awalan '0') ke format internasional ('62')
                        if (internationalFormatNumber.startsWith("0")) {
                            internationalFormatNumber = "62" + internationalFormatNumber.substring(1);
                        }

                        // Menghapus semua karakter selain angka untuk memastikan URI valid
                        String digitsOnlyNumber = internationalFormatNumber.replaceAll("\\D", "");

                        // Membuat pesan default yang informatif
                        String message = "Halo, saya ingin bertanya tentang pesanan layanan '" + service.getServiceName() + "' saya.";

                        // Membuat URI untuk intent WhatsApp
                        Uri waUri = Uri.parse("https://wa.me/" + digitsOnlyNumber + "?text=" + Uri.encode(message));
                        Intent intent = new Intent(Intent.ACTION_VIEW, waUri);

                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            // Menangani kasus jika WhatsApp tidak terinstal di perangkat
                            Toast.makeText(context, "WhatsApp tidak terpasang di perangkat Anda.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Gagal membuka WhatsApp: " + e.getMessage());
                        }
                    } else {
                        // Menangani kasus jika teknisi tidak memiliki nomor telepon
                        Toast.makeText(context, "Nomor kontak teknisi tidak tersedia.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            // KONDISI C: Status "canceled" atau status lain yang tidak ditangani
            // Tidak ada aksi yang perlu dilakukan, tombol sudah disembunyikan di awal.
        }

        private void setStatus(String status) {
            String statusText = (status == null || status.isEmpty()) ? "Unknown" : status;
            binding.textViewStatus.setText(statusText);

            Drawable background;
            int textColor;

            String lowerStatus = statusText.toLowerCase();
            if (lowerStatus.contains("completed") || lowerStatus.contains("selesai") || lowerStatus.contains("success")) {
                background = ContextCompat.getDrawable(context, R.drawable.badge_background_completed);
                textColor = ContextCompat.getColor(context, R.color.black);
            } else if (lowerStatus.contains("pending") || lowerStatus.contains("in progress")) {
                background = ContextCompat.getDrawable(context, R.drawable.badge_background_pending);
                textColor = ContextCompat.getColor(context, R.color.black);
            } else { // Canceled or other
                background = ContextCompat.getDrawable(context, R.drawable.badge_background_canceled);
                textColor = ContextCompat.getColor(context, R.color.white);
            }
            binding.textViewStatus.setBackground(background);
            binding.textViewStatus.setTextColor(textColor);
        }
    }
}
