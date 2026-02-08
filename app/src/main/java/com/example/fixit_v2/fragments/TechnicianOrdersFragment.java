package com.example.fixit_v2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fixit_v2.adapters.TechnicianOrderAdapter;
import com.example.fixit_v2.databinding.FragmentTechnicianOrdersBinding;
import com.example.fixit_v2.datasource.OrderDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.Order;

import java.util.List;

public class TechnicianOrdersFragment extends Fragment {

    private FragmentTechnicianOrdersBinding binding;
    private OrderDataSource orderDataSource;
    private ServiceDataSource serviceDataSource;
    private UserDataSource userDataSource;
    private int technicianId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTechnicianOrdersBinding.inflate(inflater, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        technicianId = preferences.getInt("technician_id", -1);

        orderDataSource = new OrderDataSource(getContext());
        serviceDataSource = new ServiceDataSource(getContext());
        userDataSource = new UserDataSource(getContext());

        binding.recyclerViewAssignedOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }

    private void loadAssignedOrders() {
        if (technicianId != -1) {
            List<Order> assignedOrders = orderDataSource.getOrdersByTechnicianId(technicianId);

            if (assignedOrders.isEmpty()) {
                Toast.makeText(getContext(), "You have no assigned orders.", Toast.LENGTH_SHORT).show();
            }

            // Pass the OrderDataSource to the adapter
            TechnicianOrderAdapter adapter = new TechnicianOrderAdapter(getContext(), assignedOrders, serviceDataSource, userDataSource, orderDataSource);
            binding.recyclerViewAssignedOrders.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "Could not find technician session.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        orderDataSource.open();
        serviceDataSource.open();
        userDataSource.open();
        loadAssignedOrders();
    }

    @Override
    public void onPause() {
        super.onPause();
        orderDataSource.close();
        serviceDataSource.close();
        userDataSource.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
