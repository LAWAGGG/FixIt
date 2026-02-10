package com.example.fixit_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.activities.CreateReviewActivity;
import com.example.fixit_v2.datasource.OrderDataSource;
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
    private OrderDataSource orderDataSource;
    private int userId;

    public BookingListAdapter(Context context, List<Order> orders, TechnicianDataSource techDS, ServiceDataSource serviceDS, OrderDataSource orderDS, int userId) {
        this.context = context;
        this.orders = orders;
        this.technicianDataSource = techDS;
        this.serviceDataSource = serviceDS;
        this.orderDataSource = orderDS;
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
        ImageButton buttonMessage;
        Button buttonCancelOrder;
        ImageView serviceIconView;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceNameView = itemView.findViewById(R.id.textViewServiceName);
            technicianNameView = itemView.findViewById(R.id.textViewTechnicianName);
            statusView = itemView.findViewById(R.id.textViewStatus);
            buttonMessage = itemView.findViewById(R.id.buttonMessage);
            buttonCancelOrder = itemView.findViewById(R.id.buttonCancelOrder);
            serviceIconView = itemView.findViewById(R.id.imageViewServiceIcon);
        }

        void bind(final Order order) {
            String status = order.getStatus();
            statusView.setText(status);

            // Dynamic Status Badge Color
            if (status != null) {
                String lowerStatus = status.toLowerCase();
                if (lowerStatus.contains("pending") || lowerStatus.contains("progress") || lowerStatus.contains("menunggu") || lowerStatus.contains("proses")) {
                    statusView.setBackgroundResource(R.drawable.badge_background_pending);
                    statusView.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                } else if (lowerStatus.contains("completed") || lowerStatus.contains("selesai")) {
                    statusView.setBackgroundResource(R.drawable.badge_background_completed);
                    statusView.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
                } else if (lowerStatus.contains("canceled") || lowerStatus.contains("dibatalkan") || lowerStatus.contains("batal")) {
                    statusView.setBackgroundResource(R.drawable.badge_background_canceled);
                    statusView.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
                } else {
                    // Default to pending if unknown
                    statusView.setBackgroundResource(R.drawable.badge_background_pending);
                    statusView.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                }
            }

            Service service = serviceDataSource.getServiceById(order.getServiceId());
            if (service != null) {
                serviceNameView.setText(service.getServiceName());
                Technician technician = technicianDataSource.getTechnicianById(service.getTechnicianId());
                
                if (technician != null) {
                    technicianNameView.setText("by " + technician.getName());
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

            // Show/hide cancel button based on status
            String lowerStatus = status != null ? status.toLowerCase() : "";
            if (lowerStatus.contains("pending") || lowerStatus.contains("progress") || lowerStatus.contains("menunggu") || lowerStatus.contains("proses")) {
                buttonCancelOrder.setVisibility(View.VISIBLE);
                buttonCancelOrder.setOnClickListener(v -> showCancelConfirmationDialog(order));
            } else {
                buttonCancelOrder.setVisibility(View.GONE);
            }

            // Handle click on the whole item for details
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, com.example.fixit_v2.activities.OrderDetailActivity.class);
                intent.putExtra("ORDER_ID", order.getId());
                context.startActivity(intent);
            });
        }
    }

    private void showCancelConfirmationDialog(Order order) {
        // Create custom dialog with opaque background
        android.app.Dialog dialog = new android.app.Dialog(context);
        dialog.setContentView(R.layout.dialog_cancel_order);
        
        // Make dialog background transparent so our custom layout shows
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setDimAmount(0.8f);
        }
        
        // Get button references
        android.widget.Button buttonYes = dialog.findViewById(R.id.buttonYes);
        android.widget.Button buttonNo = dialog.findViewById(R.id.buttonNo);
        
        // Set click listeners
        buttonYes.setOnClickListener(v -> {
            int result = orderDataSource.updateOrderStatus(order.getId(), "Canceled");
            if (result > 0) {
                order.setStatus("Canceled");
                notifyDataSetChanged();
                Toast.makeText(context, "Order canceled successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to cancel order.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        
        buttonNo.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
}
