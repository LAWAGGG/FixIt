package com.example.fixit_v2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fixit_v2.activities.CreateServiceActivity;
import com.example.fixit_v2.adapters.BestServiceAdapter;
import com.example.fixit_v2.databinding.FragmentTechnicianServicesBinding;
import com.example.fixit_v2.datasource.ReviewDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Service;

import java.util.List;

public class TechnicianServicesFragment extends Fragment {

    private FragmentTechnicianServicesBinding binding;
    private ServiceDataSource serviceDataSource;
    private ReviewDataSource reviewDataSource; // Adapter dependency
    private TechnicianDataSource technicianDataSource; // Adapter dependency
    private int technicianId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTechnicianServicesBinding.inflate(inflater, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        technicianId = preferences.getInt("technician_id", -1);

        if (technicianId == -1) {
            return binding.getRoot();
        }

        serviceDataSource = new ServiceDataSource(getContext());
        reviewDataSource = new ReviewDataSource(getContext());
        technicianDataSource = new TechnicianDataSource(getContext());

        setupRecyclerView();
        binding.fabAddService.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateServiceActivity.class);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerViewMyServices.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadMyServices() {
        List<Service> myServices = serviceDataSource.getServicesByTechnician(technicianId);
        BestServiceAdapter myServicesAdapter = new BestServiceAdapter(getContext(), myServices, technicianDataSource, reviewDataSource);
        binding.recyclerViewMyServices.setAdapter(myServicesAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceDataSource.open();
        reviewDataSource.open();
        technicianDataSource.open();
        if (technicianId != -1) {
            loadMyServices();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceDataSource.close();
        reviewDataSource.close();
        technicianDataSource.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
