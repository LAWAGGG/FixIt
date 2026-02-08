package com.example.fixit_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.databinding.CardBookingItemBinding; 
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Order;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;

import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.BookingViewHolder> {

    private final Context context;
    private final List<Order> orderList;
    private final TechnicianDataSource technicianDataSource;
    private final ServiceDataSource serviceDataSource;

    public BookingListAdapter(@NonNull Context context, @NonNull List<Order> orders, TechnicianDataSource techDS, ServiceDataSource serviceDS) {
        this.context = context;
        this.orderList = orders;
        this.technicianDataSource = techDS;
        this.serviceDataSource = serviceDS;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        CardBookingItemBinding binding = CardBookingItemBinding.inflate(inflater, parent, false);
        return new BookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(orderList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private final CardBookingItemBinding binding;

        public BookingViewHolder(CardBookingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Order order) {
            if (order == null) return;

            binding.textViewStatus.setText("Status: " + order.getStatus());
            
            Service service = serviceDataSource.getServiceById(order.getServiceId());
            if (service != null) {
                binding.textViewServiceName.setText(service.getServiceName());
                
                Technician technician = technicianDataSource.getTechnicianById(service.getTechnicianId());
                if (technician != null) {
                    binding.textViewUserName.setText(technician.getName()); // CORRECT ID
                    
                    binding.buttonAction.setOnClickListener(v -> {
                        String phoneNumber = technician.getPhoneNumber();
                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
                            if (phoneNumber.startsWith("0")) {
                                phoneNumber = "62" + phoneNumber.substring(1);
                            }
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + phoneNumber));
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Technician phone number not available.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    binding.textViewUserName.setText("Technician Not Found"); // CORRECT ID
                }
            } else {
                binding.textViewServiceName.setText("Service Not Found");
                binding.textViewUserName.setText(""); // CORRECT ID
            }
        }
    }
}
