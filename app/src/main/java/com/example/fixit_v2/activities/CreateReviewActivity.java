package com.example.fixit_v2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.databinding.ActivityCreateReviewBinding;
import com.example.fixit_v2.datasource.ReviewDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.models.Service;

public class CreateReviewActivity extends AppCompatActivity {

    private ActivityCreateReviewBinding binding;
    private ReviewDataSource reviewDataSource;
    private ServiceDataSource serviceDataSource;
    private int serviceId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        serviceId = getIntent().getIntExtra("SERVICE_ID", -1);
        userId = getIntent().getIntExtra("USER_ID", -1);

        if (serviceId == -1 || userId == -1) {
            Toast.makeText(this, "Error: Service or User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        reviewDataSource = new ReviewDataSource(this);
        serviceDataSource = new ServiceDataSource(this);

        setupToolbar();
        loadServiceInfo();
        binding.buttonSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadServiceInfo() {
        serviceDataSource.open();
        Service service = serviceDataSource.getServiceById(serviceId);
        if (service != null) {
            binding.textViewServiceName.setText("Review for " + service.getServiceName());
        }
        serviceDataSource.close();
    }

    private void submitReview() {
        int rating = (int) binding.ratingBar.getRating();
        String comment = binding.editTextComment.getText().toString();

        if (rating == 0) {
            Toast.makeText(this, "Please provide a rating of at least 1 star.", Toast.LENGTH_SHORT).show();
            return;
        }

        reviewDataSource.open();
        reviewDataSource.createReview(serviceId, userId, rating, comment);
        reviewDataSource.close();

        Toast.makeText(this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
        // Go back to the main dashboard and clear the activity stack
        Intent intent = new Intent(this, UserDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure the data source is closed if the activity is destroyed.
        // reviewDataSource.close(); // Already handled in submitReview
    }
}
