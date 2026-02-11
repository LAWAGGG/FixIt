package com.example.fixit_v2.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.ComplaintDataSource;
import com.example.fixit_v2.datasource.OrderDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Complaint;
import com.example.fixit_v2.models.Order;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;

import java.util.List;

public class AdminComplaintAdapter extends RecyclerView.Adapter<AdminComplaintAdapter.ViewHolder> {

    private final Context context;
    private final List<Complaint> complaintList;
    private final ComplaintDataSource complaintDataSource;
    private final OrderDataSource orderDataSource;

    public AdminComplaintAdapter(Context context, List<Complaint> complaints) {
        this.context = context;
        this.complaintList = complaints;
        this.complaintDataSource = new ComplaintDataSource(context);
        this.orderDataSource = new OrderDataSource(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_complaint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);
        holder.bind(complaint);
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvComplaintId, tvStatus, tvOrderInfo, tvDescription;
        android.widget.Button btnManage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComplaintId = itemView.findViewById(R.id.tvComplaintId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvOrderInfo = itemView.findViewById(R.id.tvOrderInfo);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnManage = itemView.findViewById(R.id.btnViewDetails);
        }

        void bind(Complaint complaint) {
            tvComplaintId.setText("Complaint #" + complaint.getId());
            tvStatus.setText(complaint.getStatus());
            tvDescription.setText(complaint.getDescription());
            tvOrderInfo.setText("Order ID: " + complaint.getOrderId());

            // Color coding status
            if ("Resolved".equalsIgnoreCase(complaint.getStatus()) || "Refunded".equalsIgnoreCase(complaint.getStatus())) {
                tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.GREEN));
            } else if ("Rejected".equalsIgnoreCase(complaint.getStatus())) {
                tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.RED));
            } else {
                tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF9800"))); // Orange
            }

            btnManage.setOnClickListener(v -> showActionDialog(complaint));
        }

        private void showActionDialog(Complaint complaint) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Manage Complaint #" + complaint.getId());
            
            // Show details properly including photo if we had time to inflate a custom view
            // For now, simple actions
            
            String[] options = {"View Details & Photo", "Approve Refund", "Reject Complaint"};
            
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        showDetails(complaint);
                        break;
                    case 1:
                        processRefund(complaint);
                        break;
                    case 2:
                        showRejectDialog(complaint);
                        break;
                }
            });
            builder.show();
        }

        private void showDetails(Complaint complaint) {
             AlertDialog.Builder builder = new AlertDialog.Builder(context);
             builder.setTitle("Complaint Details");
             
             // Simple layout to show text + image
             android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
             layout.setOrientation(android.widget.LinearLayout.VERTICAL);
             layout.setPadding(40, 20, 40, 20);
             
             TextView tvDesc = new TextView(context);
             tvDesc.setText(complaint.getDescription());
             tvDesc.setPadding(0, 0, 0, 20);
             layout.addView(tvDesc);
             
             if (complaint.getPhotoPath() != null) {
                 android.widget.ImageView iv = new android.widget.ImageView(context);
                 iv.setLayoutParams(new android.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500));
                 
                 java.io.File imgFile = new java.io.File(context.getFilesDir(), complaint.getPhotoPath());
                 if (imgFile.exists()) {
                     android.graphics.Bitmap myBitmap = android.graphics.BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                     iv.setImageBitmap(myBitmap);
                 }
                 layout.addView(iv);
             }
             
             builder.setView(layout);
             builder.setPositiveButton("Close", null);
             builder.show();
        }

        private void processRefund(Complaint complaint) {
            // REFUND LOGIC
            new Thread(() -> {
                orderDataSource.open();
                Order order = orderDataSource.getOrderById(complaint.getOrderId());
                orderDataSource.close();

                if (order != null) {
                    ServiceDataSource serviceDS = new ServiceDataSource(context);
                    serviceDS.open();
                    Service service = serviceDS.getServiceById(order.getServiceId());
                    serviceDS.close();

                    if (service != null) {
                        TechnicianDataSource techDS = new TechnicianDataSource(context);
                        techDS.open();
                        Technician tech = techDS.getTechnicianById(service.getTechnicianId());
                        if (tech != null) {
                            double newEarnings = tech.getEarnings() - service.getPrice();
                            techDS.updateTechnicianEarnings(tech.getId(), newEarnings);
                        }
                        techDS.close();
                    }
                    
                    // Update Order Payment Status
                    orderDataSource.open();
                    orderDataSource.updatePaymentStatus(order.getId(), "Refunded");
                    orderDataSource.close();
                }
                
                // Update Complaint Status
                complaintDataSource.open();
                complaintDataSource.updateComplaintStatusAndComment(complaint.getId(), "Refunded", "Refund approved by Admin");
                complaintDataSource.close();
                
                ((android.app.Activity)context).runOnUiThread(() -> {
                     complaint.setStatus("Refunded");
                     notifyItemChanged(getAdapterPosition());
                     Toast.makeText(context, "Refund Processed Successfully", Toast.LENGTH_SHORT).show(); 
                });
            }).start();
        }

        private void showRejectDialog(Complaint complaint) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Reject Complaint");
            builder.setMessage("Please enter the reason for rejection:");

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            builder.setView(input);

            builder.setPositiveButton("Reject", (dialog, which) -> {
                String reason = input.getText().toString();
                if (reason.isEmpty()) {
                    Toast.makeText(context, "Reason is required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                complaintDataSource.open();
                complaintDataSource.updateComplaintStatusAndComment(complaint.getId(), "Rejected", reason);
                complaintDataSource.close();
                
                complaint.setStatus("Rejected");
                notifyItemChanged(getAdapterPosition());
                Toast.makeText(context, "Complaint Rejected", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }
}
