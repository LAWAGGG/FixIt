package com.example.fixit_v2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.R;
import com.example.fixit_v2.adapters.ReviewListAdapter;
import com.example.fixit_v2.datasource.ReviewDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.Review;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ServiceDetailActivity extends AppCompatActivity {

    private TextView textViewServiceName, textViewTechnicianName, textViewServicePrice, textViewServiceDescription;
    private ListView listViewReviews;
    private Button buttonOrderNow;

    private ServiceDataSource serviceDataSource;
    private TechnicianDataSource technicianDataSource;
    private ReviewDataSource reviewDataSource;
    private UserDataSource userDataSource; // Added

    private Service currentService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        int serviceId = getIntent().getIntExtra("SERVICE_ID", -1);
        if (serviceId == -1) {
            Toast.makeText(this, "Service not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Views
        textViewServiceName = findViewById(R.id.textViewServiceName);
        textViewTechnicianName = findViewById(R.id.textViewTechnicianName);
        textViewServicePrice = findViewById(R.id.textViewServicePrice);
        textViewServiceDescription = findViewById(R.id.textViewServiceDescription);
        listViewReviews = findViewById(R.id.listViewReviews);
        buttonOrderNow = findViewById(R.id.buttonOrderNow);

        // Initialize DataSources
        serviceDataSource = new ServiceDataSource(this);
        technicianDataSource = new TechnicianDataSource(this);
        reviewDataSource = new ReviewDataSource(this);
        userDataSource = new UserDataSource(this); // Added

        // Load Data
        // Open Dbs in onResume

        buttonOrderNow.setOnClickListener(v -> {
            Intent intent = new Intent(ServiceDetailActivity.this, CreateOrderActivity.class);
            intent.putExtra("SERVICE_ID", currentService.getId());
            startActivity(intent);
        });
    }

    private void loadData() {
        int serviceId = getIntent().getIntExtra("SERVICE_ID", -1);
        currentService = serviceDataSource.getServiceById(serviceId);

        if (currentService == null) {
            Toast.makeText(this, "Failed to load service details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateServiceDetails();
        populateReviews();
    }

    private void populateServiceDetails() {
        Technician technician = technicianDataSource.getTechnicianById(currentService.getTechnicianId());

        textViewServiceName.setText(currentService.getServiceName());
        textViewServiceDescription.setText(currentService.getDescription());
        textViewTechnicianName.setText("Ditawarkan oleh: " + (technician != null ? technician.getName() : "Unknown"));

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        format.setMaximumFractionDigits(0);
        textViewServicePrice.setText(format.format(currentService.getPrice()));
    }

    private void populateReviews() {
        List<Review> reviews = reviewDataSource.getReviewsByServiceId(currentService.getId());
        // Pass the opened UserDataSource to the adapter
        ReviewListAdapter adapter = new ReviewListAdapter(this, reviews, userDataSource);
        listViewReviews.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceDataSource.open();
        technicianDataSource.open();
        reviewDataSource.open();
        userDataSource.open(); // Added
        loadData(); // Load data when activity is resumed
    }

    @Override
    protected void onPause() {
        super.onPause();
        serviceDataSource.close();
        technicianDataSource.close();
        reviewDataSource.close();
        userDataSource.close(); // Added
    }
}
