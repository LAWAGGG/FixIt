package com.example.fixit_v2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.R;
import com.example.fixit_v2.adapters.ServiceOfferAdapter;
import com.example.fixit_v2.datasource.ReviewDataSource;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class ServiceListActivity extends AppCompatActivity {

    private ListView listViewServices;
    private TextView textViewCategoryTitle;
    private ServiceDataSource serviceDataSource;
    private ServiceCategoryDataSource categoryDataSource;
    private TechnicianDataSource technicianDataSource;
    private ReviewDataSource reviewDataSource;
    private ServiceOfferAdapter adapter;
    private List<Service> serviceList;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);

        textViewCategoryTitle = findViewById(R.id.textViewCategoryTitle);
        listViewServices = findViewById(R.id.listViewServices);

        serviceDataSource = new ServiceDataSource(this);
        categoryDataSource = new ServiceCategoryDataSource(this);
        technicianDataSource = new TechnicianDataSource(this);
        reviewDataSource = new ReviewDataSource(this);
        
        if (categoryId == -1) {
            Toast.makeText(this, "Category not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listViewServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Service selectedService = serviceList.get(position);
                Intent intent = new Intent(ServiceListActivity.this, ServiceDetailActivity.class);
                intent.putExtra("SERVICE_ID", selectedService.getId());
                startActivity(intent);
            }
        });
    }
    
    private void loadServices(){
        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        ServiceCategory currentCategory = categoryDataSource.getServiceCategoryById(categoryId);
        if (currentCategory != null) {
            textViewCategoryTitle.setText(currentCategory.getCategoryName());
        }

        serviceList = serviceDataSource.getServicesByCategory(categoryId);
        adapter = new ServiceOfferAdapter(this, serviceList, technicianDataSource, reviewDataSource);
        listViewServices.setAdapter(adapter);
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
