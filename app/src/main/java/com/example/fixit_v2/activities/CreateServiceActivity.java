package com.example.fixit_v2.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.databinding.ActivityCreateServiceBinding;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class CreateServiceActivity extends AppCompatActivity {

    private ActivityCreateServiceBinding binding;
    private ServiceCategoryDataSource categoryDataSource;
    private ServiceDataSource serviceDataSource;
    private int technicianId;
    private List<ServiceCategory> categoryList;

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

        setupToolbar();
        loadCategorySpinner();
        binding.buttonAddService.setOnClickListener(v -> addService());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish()); // Go back to the previous screen
    }

    private void loadCategorySpinner() {
        categoryDataSource.open();
        categoryList = categoryDataSource.getAllServiceCategories();
        categoryDataSource.close();
        ArrayAdapter<ServiceCategory> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoryList);
        binding.spinnerCategories.setAdapter(categoryAdapter);
    }

    private void addService() {
        String categoryString = binding.spinnerCategories.getText().toString();
        String serviceName = binding.editTextServiceName.getText().toString().trim();
        String description = binding.editTextServiceDescription.getText().toString().trim();
        String priceString = binding.editTextPrice.getText().toString().trim();

        if (categoryString.isEmpty() || serviceName.isEmpty() || description.isEmpty() || priceString.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
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
            serviceDataSource.open();
            serviceDataSource.createService(serviceName, description, price, technicianId, selectedCategory.getId());
            serviceDataSource.close();
            Toast.makeText(this, "Service added successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close this activity and return to the list
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format.", Toast.LENGTH_SHORT).show();
        }
    }
}
