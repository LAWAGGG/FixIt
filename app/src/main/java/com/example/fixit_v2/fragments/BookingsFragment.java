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

import com.example.fixit_v2.adapters.BookingListAdapter;
import com.example.fixit_v2.databinding.FragmentBookingsBinding;
import com.example.fixit_v2.datasource.OrderDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Order;

import java.util.List;

public class BookingsFragment extends Fragment {

    private FragmentBookingsBinding binding;
    private OrderDataSource orderDataSource;
    private ServiceDataSource serviceDataSource;
    private TechnicianDataSource technicianDataSource;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingsBinding.inflate(inflater, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        orderDataSource = new OrderDataSource(getContext());
        serviceDataSource = new ServiceDataSource(getContext());
        technicianDataSource = new TechnicianDataSource(getContext());

        binding.recyclerViewBookings.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }

    private void loadBookings() {
        if (userId != -1) {
            List<Order> orders = orderDataSource.getOrdersByUserId(userId);
            
            if (orders.isEmpty()) {
                Toast.makeText(getContext(), "You have no bookings yet.", Toast.LENGTH_SHORT).show();
            }
            
            // Corrected constructor call with orderDataSource and userId
            BookingListAdapter adapter = new BookingListAdapter(getContext(), orders, technicianDataSource, serviceDataSource, orderDataSource, userId);
            binding.recyclerViewBookings.setAdapter(adapter);

        } else {
            Toast.makeText(getContext(), "Could not find user session.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        orderDataSource.open();
        serviceDataSource.open();
        technicianDataSource.open();
        loadBookings();
    }

    @Override
    public void onPause() {
        super.onPause();
        orderDataSource.close();
        serviceDataSource.close();
        technicianDataSource.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
