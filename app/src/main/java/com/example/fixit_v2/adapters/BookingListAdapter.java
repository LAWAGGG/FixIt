package com.example.fixit_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Order;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;

import java.util.List;

public class BookingListAdapter extends ArrayAdapter<Order> {

    private static final String TAG = "BookingListAdapter";
    private Context context;
    private TechnicianDataSource technicianDataSource;
    private ServiceDataSource serviceDataSource;

    public BookingListAdapter(@NonNull Context context, @NonNull List<Order> orders, TechnicianDataSource techDS, ServiceDataSource serviceDS) {
        super(context, R.layout.card_booking_item, orders);
        this.context = context;
        this.technicianDataSource = techDS;
        this.serviceDataSource = serviceDS;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.card_booking_item, parent, false);
        }

        TextView serviceNameView = convertView.findViewById(R.id.textViewServiceName);
        TextView technicianNameView = convertView.findViewById(R.id.textViewTechnicianName);
        TextView statusView = convertView.findViewById(R.id.textViewStatus);
        Button buttonMessage = convertView.findViewById(R.id.buttonMessage);

        Order order = getItem(position);

        if (order != null) {
            Log.d(TAG, "getView for position " + position + ": Processing Order ID: " + order.getId() + ", Service ID: " + order.getServiceId());
            statusView.setText("Status: " + order.getStatus());
            
            Service service = serviceDataSource.getServiceById(order.getServiceId());
            
            if (service != null) {
                Log.d(TAG, "--> Success: Found Service ID " + service.getId() + ": " + service.getServiceName());
                serviceNameView.setText(service.getServiceName());
                
                Technician technician = technicianDataSource.getTechnicianById(service.getTechnicianId());
                
                if (technician != null) {
                    Log.d(TAG, "--> Success: Found Technician ID " + technician.getId() + ": " + technician.getName());
                    technicianNameView.setText(technician.getName());
                    buttonMessage.setVisibility(View.VISIBLE);
                    buttonMessage.setOnClickListener(v -> {
                        String phoneNumber = technician.getPhoneNumber();
                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
                            if(phoneNumber.startsWith("0")){
                                phoneNumber = "62" + phoneNumber.substring(1);
                            }
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + phoneNumber));
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Technician phone number not available.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e(TAG, "--> FAILED: Technician NOT FOUND for ID: " + service.getTechnicianId());
                    technicianNameView.setText("Technician Not Found");
                    buttonMessage.setVisibility(View.GONE);
                }
            } else {
                Log.e(TAG, "--> FAILED: Service NOT FOUND for ID: " + order.getServiceId());
                serviceNameView.setText("Service Not Found");
                technicianNameView.setText("");
                buttonMessage.setVisibility(View.GONE);
            }
        } else {
            Log.e(TAG, "Order object at position " + position + " is null.");
        }

        return convertView;
    }
}
