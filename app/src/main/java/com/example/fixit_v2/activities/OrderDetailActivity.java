package com.example.fixit_v2.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.R;
import com.example.fixit_v2.databinding.ActivityOrderDetailBinding;
import com.example.fixit_v2.datasource.ComplaintDataSource;
import com.example.fixit_v2.datasource.OrderDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Order;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;

import java.io.File;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private ActivityOrderDetailBinding binding;
    private OrderDataSource orderDataSource;
    private ServiceDataSource serviceDataSource;
    private TechnicianDataSource technicianDataSource;
    private int orderId;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Order not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderDataSource = new OrderDataSource(this);
        serviceDataSource = new ServiceDataSource(this);
        technicianDataSource = new TechnicianDataSource(this);

        setupToolbar();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadOrderDetails() {
        orderDataSource.open();
        serviceDataSource.open();
        technicianDataSource.open();

        order = orderDataSource.getOrderById(orderId);

        if (order == null) {
            Toast.makeText(this, "Failed to load order.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Service service = serviceDataSource.getServiceById(order.getServiceId());
        Technician technician = (service != null) ? technicianDataSource.getTechnicianById(service.getTechnicianId()) : null;

        if (service != null) {
            binding.textViewServiceName.setText(service.getServiceName());
            binding.textViewPrice.setText(String.format(Locale.GERMAN, "Rp %,d", (long) service.getPrice()));
        }

        if (technician != null) {
            binding.textViewTechnician.setText(technician.getName());
            binding.buttonAction.setOnClickListener(v -> openWhatsApp(technician.getPhoneNumber()));
        } else {
            binding.textViewTechnician.setText("Searching for technician...");
            binding.buttonAction.setEnabled(false);
        }

        binding.textViewAddress.setText(order.getAddress());
        binding.textViewDate.setText(order.getOrderDate());
        binding.textViewNotes.setText(order.getNotes() != null && !order.getNotes().isEmpty() ? order.getNotes() : "No notes provided.");
        
        // Payment Info
        binding.textViewPaymentMethod.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "Not set");
        String pStatus = order.getPaymentStatus() != null ? order.getPaymentStatus() : "Waiting";
        binding.textViewPaymentStatus.setText(pStatus.toUpperCase());
        if (pStatus.equalsIgnoreCase("Paid")) {
            binding.textViewPaymentStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            binding.textViewPaymentStatus.setTextColor(getResources().getColor(R.color.secondary));
        }

        setupTimeline(order.getStatus());
        setupCompletionProof(order.getCompletionImage());

        // Review and Complaint Button Logic
        if (order.getStatus().equalsIgnoreCase("Completed") || order.getStatus().equalsIgnoreCase("Finished")) {
            binding.buttonReview.setVisibility(View.VISIBLE);
            binding.buttonReview.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateReviewActivity.class);
                intent.putExtra("SERVICE_ID", order.getServiceId());
                intent.putExtra("USER_ID", getUserIdFromSession());
                startActivity(intent);
            });

            // Complaint Logic
            ComplaintDataSource complaintDataSource = new ComplaintDataSource(this);
            complaintDataSource.open();
            com.example.fixit_v2.models.Complaint existingComplaint = complaintDataSource.getComplaintByOrderId(orderId);
            
            binding.buttonComplaint.setVisibility(View.VISIBLE);
            if (existingComplaint != null) {
                binding.buttonComplaint.setText("Complaint Filed (" + existingComplaint.getStatus() + ")");
                binding.buttonComplaint.setEnabled(false);
                binding.buttonComplaint.setAlpha(0.7f);
            } else {
                binding.buttonComplaint.setText("File Complaint");
                binding.buttonComplaint.setEnabled(true);
                binding.buttonComplaint.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ComplaintActivity.class);
                    intent.putExtra("ORDER_ID", orderId);
                    startActivity(intent);
                });
            }
            complaintDataSource.close();

        } else {
            binding.buttonReview.setVisibility(View.GONE);
            binding.buttonComplaint.setVisibility(View.GONE);
        }

        orderDataSource.close();
        serviceDataSource.close();
        technicianDataSource.close();
    }
    
    private int getUserIdFromSession() {
        return getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1);
    }

    private void setupTimeline(String status) {
        binding.layoutStatusTimeline.removeAllViews();
        String[] statuses = {"Pending", "Accepted", "On the Way", "In Progress", "Completed"};
        String[] descriptions = {
            "Waiting for technician acceptance",
            "Technician has accepted your order",
            "Technician is heading to your location",
            "Service is currently being performed",
            "Service completed successfully"
        };

        String currentStatus = (status == null) ? "Pending" : status;
        
        if (currentStatus.equalsIgnoreCase("Canceled")) {
            addTimelineStep("Canceled", "The order has been canceled.", true, true, false);
            return;
        }

        int currentStepIndex = -1;
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(currentStatus)) {
                currentStepIndex = i;
                break;
            }
        }

        for (int i = 0; i < statuses.length; i++) {
            boolean isPast = i < currentStepIndex;
            boolean isCurrent = i == currentStepIndex;
            boolean isFuture = i > currentStepIndex;
            boolean isLast = (i == statuses.length - 1);

            // In our simple logic, past and current are "highlighted"
            addTimelineStep(statuses[i], descriptions[i], isCurrent, isPast, isLast);
        }
    }

    private void addTimelineStep(String name, String description, boolean isCurrent, boolean isPast, boolean isLast) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_status_timeline, binding.layoutStatusTimeline, false);
        TextView tvName = view.findViewById(R.id.textViewStatusName);
        TextView tvDesc = view.findViewById(R.id.textViewStatusDescription);
        View dot = view.findViewById(R.id.viewDot);
        View line = view.findViewById(R.id.viewLine);

        tvName.setText(name);
        
        // Only show description for current or past steps to reduce clutter
        if (isCurrent || isPast) {
            tvDesc.setVisibility(View.VISIBLE);
            tvDesc.setText(description);
            
            dot.setBackgroundResource(R.drawable.timeline_dot_active);
            tvName.setTextColor(getResources().getColor(R.color.white));
            tvDesc.setTextColor(getResources().getColor(R.color.white));
            tvDesc.setAlpha(0.7f);
            
            // Highlight the line if the NEXT step is also past or if this is current
            // Actually, highlight line if this step is past (meaning connection to next is "active")
            if (isPast) {
                line.setBackgroundColor(getResources().getColor(R.color.secondary));
            }
        } else {
            // Future step
            tvDesc.setVisibility(View.GONE);
            dot.setBackgroundResource(R.drawable.timeline_dot_inactive);
            tvName.setTextColor(getResources().getColor(R.color.white));
            tvName.setAlpha(0.3f);
            line.setBackgroundColor(0x20FFFFFF); // Dim line
        }

        if (isCurrent) {
            tvName.setTextSize(18);
            tvName.setAlpha(1.0f);
            tvName.setTypeface(null, android.graphics.Typeface.BOLD);
            dot.setScaleX(1.2f);
            dot.setScaleY(1.2f);
        }

        if (isLast) {
            line.setVisibility(View.GONE);
        }

        binding.layoutStatusTimeline.addView(view);
    }

    private void setupCompletionProof(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            binding.layoutCompletion.setVisibility(View.VISIBLE);
            File imageFile = new File(getFilesDir(), imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                binding.imageViewCompletion.setImageBitmap(bitmap);
            }
        } else {
            binding.layoutCompletion.setVisibility(View.GONE);
        }
    }

    private void openWhatsApp(String phone) {
        if (phone != null && !phone.isEmpty()) {
            String phoneNumber = phone.startsWith("0") ? "62" + phone.substring(1) : phone;
            phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + phoneNumber));
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderDetails();
    }
}
