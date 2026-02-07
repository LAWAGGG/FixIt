package com.example.fixit_v2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fixit_v2.R;
import com.example.fixit_v2.activities.CreateReviewActivity;
import com.example.fixit_v2.adapters.BookingListAdapter;
import com.example.fixit_v2.datasource.OrderDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Order;

import java.util.List;

public class BookingsFragment extends Fragment {

    private static final String TAG = "BookingsFragment";
    private ListView listViewBookings;
    private OrderDataSource orderDataSource;
    private ServiceDataSource serviceDataSource;
    private TechnicianDataSource technicianDataSource;
    private List<Order> orders;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        listViewBookings = view.findViewById(R.id.listViewBookings);
        
        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        orderDataSource = new OrderDataSource(getContext());
        serviceDataSource = new ServiceDataSource(getContext());
        technicianDataSource = new TechnicianDataSource(getContext());

        return view;
    }

    private void loadBookings() {
        if (userId != -1) {
            orders = orderDataSource.getOrdersByUserId(userId);
            Log.d(TAG, "Found " + orders.size() + " bookings for User ID: " + userId);
            
            if (orders.isEmpty()){
                Toast.makeText(getContext(), "You have no bookings yet.", Toast.LENGTH_LONG).show();
            }

            BookingListAdapter adapter = new BookingListAdapter(getContext(), orders, technicianDataSource, serviceDataSource);
            listViewBookings.setAdapter(adapter);

            listViewBookings.setOnItemClickListener((parent, view, position, id) -> {
                Order selectedOrder = orders.get(position);
                if ("Selesai".equals(selectedOrder.getStatus())) {
                    Intent intent = new Intent(getActivity(), CreateReviewActivity.class);
                    intent.putExtra("SERVICE_ID", selectedOrder.getServiceId());
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "You can only review completed orders.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Could not find user session.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "User ID is -1. Cannot load bookings.");
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
}
