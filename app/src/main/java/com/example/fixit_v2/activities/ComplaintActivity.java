package com.example.fixit_v2.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.databinding.ActivityComplaintBinding;
import com.example.fixit_v2.datasource.ComplaintDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ComplaintActivity extends AppCompatActivity {

    private ActivityComplaintBinding binding;
    private ComplaintDataSource complaintDataSource;
    private int orderId;
    private String selectedPhotoPath;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComplaintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Order ID missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        complaintDataSource = new ComplaintDataSource(this);
        setupToolbar();
        setupPhotoUpload();
        
        binding.buttonSubmit.setOnClickListener(v -> submitComplaint());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupPhotoUpload() {
        binding.layoutPhoto.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                binding.imageViewPreview.setImageBitmap(bitmap);
                binding.imageViewPreview.setVisibility(View.VISIBLE);
                binding.layoutPlaceholder.setVisibility(View.GONE);
                
                selectedPhotoPath = saveImageToInternalStorage(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "COMPLAINT_" + timeStamp + ".jpg";
            
            File file = new File(getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos); // Compress to 50%
            fos.close();
            
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void submitComplaint() {
        String description = binding.editTextDescription.getText().toString().trim();
        
        if (description.isEmpty()) {
            Toast.makeText(this, "Please describe the problem.", Toast.LENGTH_SHORT).show();
            return;
        }

        complaintDataSource.open();
        complaintDataSource.createComplaint(orderId, description, selectedPhotoPath);
        complaintDataSource.close();

        Toast.makeText(this, "Complaint submitted successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
