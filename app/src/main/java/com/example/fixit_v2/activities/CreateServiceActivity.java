package com.example.fixit_v2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.databinding.ActivityCreateServiceBinding;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.models.ServiceCategory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class CreateServiceActivity extends AppCompatActivity {

    private ActivityCreateServiceBinding binding;
    private ServiceCategoryDataSource categoryDataSource;
    private ServiceDataSource serviceDataSource;
    private int technicianId;
    private List<ServiceCategory> categoryList;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private Uri selectedQrisUri;
    private ActivityResultLauncher<Intent> qrisPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        technicianId = preferences.getInt("technician_id", -1);

        if (technicianId == -1) {
            Toast.makeText(this, "Error: Technician ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        categoryDataSource = new ServiceCategoryDataSource(this);
        serviceDataSource = new ServiceDataSource(this);

        setupImagePicker();
        setupQrisPicker();
        setupToolbar();
        loadCategorySpinner();
        
        binding.buttonSelectImage.setOnClickListener(v -> openImagePicker());
        binding.buttonSelectQris.setOnClickListener(v -> openQrisPicker());
        binding.buttonAddService.setOnClickListener(v -> addService());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.imageViewPreview.setImageURI(selectedImageUri);
                    binding.imageViewPreview.setVisibility(View.VISIBLE);
                    binding.buttonSelectImage.setText("Change Service Image");
                }
            }
        );
    }

    private void setupQrisPicker() {
        qrisPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedQrisUri = result.getData().getData();
                    binding.imageViewQrisPreview.setImageURI(selectedQrisUri);
                    binding.imageViewQrisPreview.setVisibility(View.VISIBLE);
                    binding.buttonSelectQris.setText("Change QRIS Image");
                }
            }
        );
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void openQrisPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        qrisPickerLauncher.launch(intent);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadCategorySpinner() {
        categoryDataSource.open();
        categoryList = categoryDataSource.getAllServiceCategories();
        categoryDataSource.close();
        ArrayAdapter<ServiceCategory> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoryList);
        binding.spinnerCategories.setAdapter(categoryAdapter);
    }

    private String saveImageToInternalStorage(Uri uri, String serviceName, boolean isQris) {
        if (uri == null) {
            return "";
        }

        try {
            // Create services directory in internal storage
            File servicesDir = new File(getFilesDir(), "services");
            if (!servicesDir.exists()) {
                servicesDir.mkdirs();
            }

            // Create filename from service name (sanitize)
            String sanitizedName = serviceName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
            String prefix = isQris ? "qris_" : "service_";
            String filename = prefix + sanitizedName + "_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(servicesDir, filename);

            // Copy image from URI to internal storage
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            outputStream.close();
            inputStream.close();

            // Return relative path for database
            return "services/" + filename;
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    private void addService() {
        String categoryString = binding.spinnerCategories.getText().toString();
        String serviceName = binding.editTextServiceName.getText().toString().trim();
        String description = binding.editTextServiceDescription.getText().toString().trim();
        String priceString = binding.editTextPrice.getText().toString().trim();

        if (categoryString.isEmpty() || serviceName.isEmpty() || description.isEmpty() || priceString.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (selectedQrisUri == null) {
             Toast.makeText(this, "QRIS Image is required!", Toast.LENGTH_LONG).show();
             return;
        }

        ServiceCategory selectedCategory = null;
        for (ServiceCategory category : categoryList) {
            if (category.toString().equals(categoryString)) {
                selectedCategory = category;
                break;
            }
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Please select a valid category.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceString);
            
            // Save images and get paths
            String imagePath = (selectedImageUri != null) ? saveImageToInternalStorage(selectedImageUri, serviceName, false) : "";
            String qrisPath = saveImageToInternalStorage(selectedQrisUri, serviceName, true);
            
            serviceDataSource.open();
            // Using new createService method with qrisPath
            serviceDataSource.createService(serviceName, description, price, technicianId, selectedCategory.getId(), imagePath, qrisPath);
            serviceDataSource.close();
            
            Toast.makeText(this, "Service added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format.", Toast.LENGTH_SHORT).show();
        }
    }
}
