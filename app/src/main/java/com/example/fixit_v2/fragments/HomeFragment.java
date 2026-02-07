package com.example.fixit_v2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fixit_v2.R;
import com.example.fixit_v2.activities.ServiceListActivity;
import com.example.fixit_v2.activities.UserDashboardActivity;
import com.example.fixit_v2.adapters.BestServiceAdapter;
import com.example.fixit_v2.datasource.ReviewDataSource;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private GridLayout gridLayoutCategories;
    private LinearLayout layoutBestServices;
    private ServiceCategoryDataSource serviceCategoryDataSource;
    private ServiceDataSource serviceDataSource;
    private ReviewDataSource reviewDataSource;
    private TechnicianDataSource technicianDataSource;
    private int userId;

    private static class RatedService {
        Service service;
        float rating;
        RatedService(Service service, float rating) { this.service = service; this.rating = rating; }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        gridLayoutCategories = view.findViewById(R.id.gridLayoutCategories);
        layoutBestServices = view.findViewById(R.id.layoutBestServices);

        serviceCategoryDataSource = new ServiceCategoryDataSource(getContext());
        serviceDataSource = new ServiceDataSource(getContext());
        reviewDataSource = new ReviewDataSource(getContext());
        technicianDataSource = new TechnicianDataSource(getContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceCategoryDataSource.open();
        serviceDataSource.open();
        reviewDataSource.open();
        technicianDataSource.open();
        
        populateCategoriesGrid();
        populateBestServices();
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceCategoryDataSource.close();
        serviceDataSource.close();
        reviewDataSource.close();
        technicianDataSource.close();
    }

    private void populateCategoriesGrid() {
        gridLayoutCategories.removeAllViews();
        List<ServiceCategory> allCategories = serviceCategoryDataSource.getAllServiceCategories();
        List<ServiceCategory> displayedCategories = new ArrayList<>();

        int limit = Math.min(allCategories.size(), 8);
        if (limit > 0) {
            displayedCategories.addAll(allCategories.subList(0, limit));
        }

        displayedCategories.add(new ServiceCategory(-99, "All"));

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (ServiceCategory category : displayedCategories) {
            View itemView = inflater.inflate(R.layout.grid_item_category, gridLayoutCategories, false);
            
            ImageView icon = itemView.findViewById(R.id.imageViewCategoryIcon);
            TextView name = itemView.findViewById(R.id.textViewCategoryName);
            name.setText(category.getCategoryName());

            if (category.getId() == -99) {
                icon.setImageResource(android.R.drawable.ic_menu_sort_by_size);
                itemView.setOnClickListener(v -> {
                    if (getActivity() instanceof UserDashboardActivity) {
                        ((UserDashboardActivity) getActivity()).navigateToCategories();
                    }
                });
            } else {
                icon.setImageResource(R.mipmap.ic_launcher);
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), ServiceListActivity.class);
                    intent.putExtra("CATEGORY_ID", category.getId());
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);
                });
            }
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            itemView.setLayoutParams(params);
            
            gridLayoutCategories.addView(itemView);
        }
    }

    private void populateBestServices() {
        layoutBestServices.removeAllViews();
        List<Service> allServices = serviceDataSource.getAllServices();
        List<RatedService> ratedServices = new ArrayList<>();

        for (Service service : allServices) {
            float avgRating = reviewDataSource.getAverageRating(service.getId());
            if (avgRating > 0) {
                ratedServices.add(new RatedService(service, avgRating));
            }
        }

        Collections.sort(ratedServices, (o1, o2) -> Float.compare(o2.rating, o1.rating));

        List<Service> bestServices = new ArrayList<>();
        for (int i = 0; i < Math.min(ratedServices.size(), 5); i++) {
            bestServices.add(ratedServices.get(i).service);
        }

        BestServiceAdapter bestServiceAdapter = new BestServiceAdapter(getContext(), bestServices, technicianDataSource, reviewDataSource);
        
        for (int i = 0; i < bestServiceAdapter.getCount(); i++) {
            View cardView = bestServiceAdapter.getView(i, null, layoutBestServices);
            layoutBestServices.addView(cardView);
        }
    }
}
