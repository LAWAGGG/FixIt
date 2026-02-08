package com.example.fixit_v2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fixit_v2.adapters.ReviewAdapter;
import com.example.fixit_v2.databinding.ActivityServiceDetailBinding;
import com.example.fixit_v2.datasource.ReviewDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Review;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;

import java.util.List;
import java.util.Locale;

public class ServiceDetailActivity extends AppCompatActivity {

    private ActivityServiceDetailBinding binding;
    private ServiceDataSource serviceDataSource;
    private TechnicianDataSource technicianDataSource;
    private ReviewDataSource reviewDataSource;
    private ReviewAdapter reviewAdapter;
    private int serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        serviceId = getIntent().getIntExtra("SERVICE_ID", -1);
        if (serviceId == -1) {
            Toast.makeText(this, "Service not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        serviceDataSource = new ServiceDataSource(this);
        technicianDataSource = new TechnicianDataSource(this);
        reviewDataSource = new ReviewDataSource(this);

        setupToolbar();
        setupRecyclerView();
        setupFab();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        binding.recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewReviews.setNestedScrollingEnabled(false);
    }

    private void setupFab() {
        binding.fabOrderNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateOrderActivity.class);
            intent.putExtra("SERVICE_ID", serviceId);
            startActivity(intent);
        });
    }

    private void loadServiceDetails() {
        Service service = serviceDataSource.getServiceById(serviceId);
        if (service == null) {
            Toast.makeText(this, "Failed to load service details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.toolbarLayout.setTitle(service.getServiceName());
        binding.textViewServiceName.setText(service.getServiceName());
        binding.textViewServiceDescription.setText(service.getDescription());
        binding.textViewServicePrice.setText(String.format(Locale.GERMAN, "Rp %,d", (long) service.getPrice()));

        Technician technician = technicianDataSource.getTechnicianById(service.getTechnicianId());
        if (technician != null) {
            binding.textViewTechnicianName.setText("by " + technician.getName());
        }

        // Load images with Picasso/Glide here
        // Picasso.get().load(service.getImageUrl()).into(binding.imageViewServiceImage);

        List<Review> reviews = reviewDataSource.getReviewsByServiceId(serviceId);
        reviewAdapter = new ReviewAdapter(this, reviews);
        binding.recyclerViewReviews.setAdapter(reviewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceDataSource.open();
        technicianDataSource.open();
        reviewDataSource.open();
        loadServiceDetails();
    }

    @Override
    protected void onPause() {
        super.onPause();
        serviceDataSource.close();
        technicianDataSource.close();
        reviewDataSource.close();
        if (reviewAdapter != null) {
            reviewAdapter.closeDataSource(); // Important to close the adapter's data source
        }
    }
}
