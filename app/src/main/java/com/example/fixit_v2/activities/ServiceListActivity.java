package com.example.fixit_v2.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fixit_v2.adapters.BestServiceAdapter;
import com.example.fixit_v2.databinding.ActivityServiceListBinding;
import com.example.fixit_v2.datasource.ReviewDataSource;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class ServiceListActivity extends AppCompatActivity {

    private ActivityServiceListBinding binding;
    private ServiceDataSource serviceDataSource;
    private ServiceCategoryDataSource categoryDataSource;
    private TechnicianDataSource technicianDataSource;
    private ReviewDataSource reviewDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);

        serviceDataSource = new ServiceDataSource(this);
        categoryDataSource = new ServiceCategoryDataSource(this);
        technicianDataSource = new TechnicianDataSource(this);
        reviewDataSource = new ReviewDataSource(this);

        setupToolbar();
        binding.recyclerViewServices.setLayoutManager(new LinearLayoutManager(this));

        if (categoryId == -1) {
            Toast.makeText(this, "Category not found!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadServices() {
        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        ServiceCategory currentCategory = categoryDataSource.getServiceCategoryById(categoryId);
        if (currentCategory != null) {
            binding.toolbar.setTitle(currentCategory.getCategoryName());
        }

        List<Service> serviceList = serviceDataSource.getServicesByCategory(categoryId);
        if (serviceList.isEmpty()) {
            Toast.makeText(this, "No services found in this category.", Toast.LENGTH_SHORT).show();
        }

        BestServiceAdapter adapter = new BestServiceAdapter(this, serviceList, technicianDataSource, reviewDataSource, categoryDataSource);
        binding.recyclerViewServices.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceDataSource.open();
        categoryDataSource.open();
        technicianDataSource.open();
        reviewDataSource.open();
        loadServices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        serviceDataSource.close();
        categoryDataSource.close();
        technicianDataSource.close();
        reviewDataSource.close();
    }
}
