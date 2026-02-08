package com.example.fixit_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.databinding.CardTechnicianOrderBinding;
import com.example.fixit_v2.datasource.OrderDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.Order;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.User;

import java.util.List;

public class TechnicianOrderAdapter extends RecyclerView.Adapter<TechnicianOrderAdapter.ViewHolder> {

    private final Context context;
    private final List<Order> orderList;
    private final ServiceDataSource serviceDataSource;
    private final UserDataSource userDataSource;
    private final OrderDataSource orderDataSource;

    public TechnicianOrderAdapter(Context context, List<Order> orders, ServiceDataSource serviceDS, UserDataSource userDS, OrderDataSource orderDS) {
        this.context = context;
        this.orderList = orders;
        this.serviceDataSource = serviceDS;
        this.userDataSource = userDS;
        this.orderDataSource = orderDS;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        CardTechnicianOrderBinding binding = CardTechnicianOrderBinding.inflate(inflater, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private final CardTechnicianOrderBinding binding;

        public ViewHolder(CardTechnicianOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Order order) {
            if (order == null) return;

            binding.textViewStatus.setText("Status: " + order.getStatus());
            binding.textViewAddress.setText(order.getAddress());
            binding.textViewOrderDate.setText(order.getOrderDate());

            Service service = serviceDataSource.getServiceById(order.getServiceId());
            if (service != null) {
                binding.textViewServiceName.setText(service.getServiceName());
            }

            User customer = userDataSource.getUserById(order.getUserId());
            if (customer != null) {
                binding.textViewCustomerName.setText("Customer: " + customer.getUsername());
                binding.buttonAction.setImageResource(R.drawable.ic_whatsapp);
                binding.buttonAction.setOnClickListener(v -> openWhatsApp(customer.getPhone()));
            }

            String currentStatus = order.getStatus().toLowerCase();
            if (currentStatus.contains("completed") || currentStatus.contains("canceled")) {
                binding.textViewStatus.setOnClickListener(null);
                binding.textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); // No arrow
            } else {
                binding.textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0); // Add arrow
                binding.textViewStatus.setOnClickListener(v -> showStatusMenu(v, order, currentStatus));
            }
        }

        private void showStatusMenu(View anchor, final Order order, String currentStatus) {
            PopupMenu popup = new PopupMenu(context, anchor);
            popup.getMenuInflater().inflate(R.menu.technician_order_status_menu, popup.getMenu());

            if (currentStatus.contains("in progress")) {
                popup.getMenu().findItem(R.id.status_cancel).setVisible(false);
            }

            popup.setOnMenuItemClickListener(item -> {
                String newStatus = null;
                int itemId = item.getItemId();
                if (itemId == R.id.status_in_progress) {
                    newStatus = "In Progress";
                } else if (itemId == R.id.status_completed) {
                    newStatus = "Completed";
                } else if (itemId == R.id.status_cancel) {
                    newStatus = "Canceled";
                }
                
                if (newStatus != null && !newStatus.equals(order.getStatus())) {
                    updateOrderStatus(order, newStatus);
                }
                return true;
            });

            popup.show();
        }

        private void openWhatsApp(String phone) {
            if (phone != null && !phone.isEmpty()) {
                String phoneNumber = phone.startsWith("0") ? "62" + phone.substring(1) : phone;
                phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + phoneNumber));
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Customer phone number not available.", Toast.LENGTH_SHORT).show();
            }
        }

        private void updateOrderStatus(Order order, String newStatus) {
            orderDataSource.open();
            int rowsAffected = orderDataSource.updateOrderStatus(order.getId(), newStatus);
            orderDataSource.close();

            if (rowsAffected > 0) {
                Toast.makeText(context, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                order.setStatus(newStatus);
                notifyItemChanged(getAdapterPosition());
            } else {
                Toast.makeText(context, "Failed to update status.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
