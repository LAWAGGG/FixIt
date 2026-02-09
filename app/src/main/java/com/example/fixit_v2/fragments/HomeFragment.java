package com.example.fixit_v2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fixit_v2.R;
import com.example.fixit_v2.adapters.BestServiceAdapter;
import com.example.fixit_v2.adapters.HomeCategoryAdapter;
import com.example.fixit_v2.databinding.FragmentHomeBinding;
import com.example.fixit_v2.datasource.ReviewDataSource;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.ServiceCategory;
import com.example.fixit_v2.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ServiceCategoryDataSource serviceCategoryDataSource;
    private ServiceDataSource serviceDataSource;
    private ReviewDataSource reviewDataSource;
    private TechnicianDataSource technicianDataSource;
    private int userId;
    private BestServiceAdapter bestServiceAdapter;
    private boolean isCurrentlySearching = false; // Flag to prevent infinite loop

    private static class RatedService implements Comparable<RatedService> {
        Service service;
        float rating;

        RatedService(Service service, float rating) {
            this.service = service;
            this.rating = rating;
        }

        @Override
        public int compareTo(RatedService other) {
            return Float.compare(other.rating, this.rating); // Sort descending
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        serviceCategoryDataSource = new ServiceCategoryDataSource(getContext());
        serviceDataSource = new ServiceDataSource(getContext());
        reviewDataSource = new ReviewDataSource(getContext());
        technicianDataSource = new TechnicianDataSource(getContext());

        setupRecyclerViews();
        setupSearch();
        return binding.getRoot();
    }

    private void setupRecyclerViews() {
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_small);
        binding.recyclerViewCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));
        if (binding.recyclerViewCategories.getItemDecorationCount() == 0) { // Prevent adding multiple times
            binding.recyclerViewCategories.addItemDecoration(new GridSpacingItemDecoration(4, spacingInPixels, true));
        }
        binding.recyclerViewBestServices.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewBestServices.setNestedScrollingEnabled(false);
    }

    private void setupSearch() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                binding.searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    if (isCurrentlySearching) {
                        resetToDefaultView();
                    }
                } else {
                    performSearch(newText);
                }
                return true;
            }
        });
    }

    private void performSearch(String keyword) {
        isCurrentlySearching = true;
        binding.recyclerViewCategories.setVisibility(View.GONE);
        binding.bestServicesTitle.setText("Search Results");

        List<Service> searchResult = serviceDataSource.searchServicesByName(keyword);
        updateServiceListWithRatings(searchResult, false);
    }

    private void resetToDefaultView() {
        isCurrentlySearching = false;
        if (binding == null) return;
        binding.recyclerViewCategories.setVisibility(View.VISIBLE);
        binding.bestServicesTitle.setText("Best Services");
        populateBestServices();
    }

    private void updateServiceListWithRatings(List<Service> services, boolean limitResults) {
        List<RatedService> ratedServices = new ArrayList<>();
        for (Service service : services) {
            float avgRating = reviewDataSource.getAverageRating(service.getId());
            ratedServices.add(new RatedService(service, avgRating));
        }

        Collections.sort(ratedServices);

        List<Service> sortedServices = new ArrayList<>();
        for (RatedService rs : ratedServices) {
            sortedServices.add(rs.service);
        }

        List<Service> finalList = sortedServices;
        if (limitResults && sortedServices.size() > 5) {
            finalList = sortedServices.subList(0, 5);
        }

        bestServiceAdapter = new BestServiceAdapter(getContext(), finalList, technicianDataSource, reviewDataSource);
        binding.recyclerViewBestServices.setAdapter(bestServiceAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceDataSource.open();
        technicianDataSource.open();
        reviewDataSource.open();
        serviceCategoryDataSource.open();
        
        populateCategories();
        if (!isCurrentlySearching) {
            populateBestServices();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceDataSource.close();
        technicianDataSource.close();
        reviewDataSource.close();
        serviceCategoryDataSource.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateCategories() {
        List<ServiceCategory> allCategories = serviceCategoryDataSource.getAllServiceCategories();
        List<ServiceCategory> displayedCategories = new ArrayList<>();
        int limit = Math.min(allCategories.size(), 7);
        if (limit > 0) {
            displayedCategories.addAll(allCategories.subList(0, limit));
        }
        displayedCategories.add(new ServiceCategory(-99, "All"));
        HomeCategoryAdapter adapter = new HomeCategoryAdapter(getContext(), displayedCategories, userId);
        binding.recyclerViewCategories.setAdapter(adapter);
    }

    private void populateBestServices() {
        List<Service> servicesFromDb = serviceDataSource.getAllServices();
        List<Service> highRatedServices = new ArrayList<>();
        for (Service service : servicesFromDb) {
            if (reviewDataSource.getAverageRating(service.getId()) > 3.5) {
                highRatedServices.add(service);
            }
        }
        updateServiceListWithRatings(highRatedServices, true);
    }
}
