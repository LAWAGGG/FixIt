package com.example.fixit_v2.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.ReviewDataSource;

public class CreateReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText editTextComment;
    private Button buttonSubmitReview;

    private ReviewDataSource reviewDataSource;
    private int serviceId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);

        serviceId = getIntent().getIntExtra("SERVICE_ID", -1);
        userId = getIntent().getIntExtra("USER_ID", 1); // Fallback to 1 for now

        if (serviceId == -1) {
            Toast.makeText(this, "Error: Service ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ratingBar = findViewById(R.id.ratingBar);
        editTextComment = findViewById(R.id.editTextComment);
        buttonSubmitReview = findViewById(R.id.buttonSubmitReview);

        reviewDataSource = new ReviewDataSource(this);
        reviewDataSource.open();

        buttonSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });
    }

    private void submitReview() {
        int rating = (int) ratingBar.getRating();
        String comment = editTextComment.getText().toString();

        if (rating == 0) {
            Toast.makeText(this, "Please provide a rating.", Toast.LENGTH_SHORT).show();
            return;
        }

        reviewDataSource.createReview(serviceId, userId, rating, comment);
        Toast.makeText(this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onResume() {
        reviewDataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        reviewDataSource.close();
        super.onPause();
    }
}
