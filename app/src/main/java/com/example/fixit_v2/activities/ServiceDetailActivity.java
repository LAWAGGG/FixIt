package com.example.fixit_v2.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fixit_v2.R;
import com.example.fixit_v2.adapters.ReviewAdapter;
import com.example.fixit_v2.databinding.ActivityServiceDetailBinding;
import com.example.fixit_v2.datasource.ReviewDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Review;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;

import java.io.File;
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

        binding.textViewServiceName.setText(service.getServiceName());
        binding.textViewServiceDescription.setText(service.getDescription());
        binding.textViewServicePrice.setText(String.format(Locale.GERMAN, "Rp %,d", (long) service.getPrice()));

        // Set Toolbar title (standard navbar style)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(service.getServiceName());
        }

        Technician technician = technicianDataSource.getTechnicianById(service.getTechnicianId());
        if (technician != null) {
            binding.textViewTechnicianName.setText("by " + technician.getName());
        }

        // Load service image from imagePath
        String imagePath = service.getImagePath();
        boolean imageLoaded = false;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Bitmap bitmap = null;
                
                // Check if image is from assets or internal storage
                if (imagePath.startsWith("images/")) {
                    // Load from assets
                    java.io.InputStream inputStream = getAssets().open(imagePath);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                } else {
                    // Load from internal storage (relative path)
                    File imageFile = new File(getFilesDir(), imagePath);
                    if (imageFile.exists()) {
                        bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    }
                }
                
                if (bitmap != null) {
                    binding.imageViewServiceImage.setImageBitmap(bitmap);
                    binding.imageViewServiceImage.setVisibility(android.view.View.VISIBLE);
                    imageLoaded = true;
                }
            } catch (java.io.IOException e) {
                // If image error, will treat as not loaded
            }
        }

        if (!imageLoaded) {
            // No image: Hide the ImageView and collapse the AppBar to show only navbar
            binding.imageViewServiceImage.setVisibility(android.view.View.GONE);
            binding.appBar.setExpanded(false, false);
            // Disable scroll flags if no image exists so it stays as a static navbar
            com.google.android.material.appbar.AppBarLayout.LayoutParams params = 
                (com.google.android.material.appbar.AppBarLayout.LayoutParams) binding.toolbarLayout.getLayoutParams();
            params.setScrollFlags(0); // Standard static toolbar behavior
            binding.toolbarLayout.setLayoutParams(params);
        } else {
            binding.imageViewServiceImage.setVisibility(android.view.View.VISIBLE);
            binding.appBar.setExpanded(true, true);
            // Re-enable scroll flags if image exists
            com.google.android.material.appbar.AppBarLayout.LayoutParams params = 
                (com.google.android.material.appbar.AppBarLayout.LayoutParams) binding.toolbarLayout.getLayoutParams();
            params.setScrollFlags(com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | 
                                 com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            binding.toolbarLayout.setLayoutParams(params);
        }

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
