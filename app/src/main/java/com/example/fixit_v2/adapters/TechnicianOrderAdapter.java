package com.example.fixit_v2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.Order;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.User;

import java.util.List;

public class TechnicianOrderAdapter extends ArrayAdapter<Order> {

    private Context context;
    private ServiceDataSource serviceDataSource;
    private UserDataSource userDataSource;

    public TechnicianOrderAdapter(@NonNull Context context, @NonNull List<Order> orders, ServiceDataSource serviceDS, UserDataSource userDS) {
        super(context, R.layout.card_technician_order, orders);
        this.context = context;
        this.serviceDataSource = serviceDS;
        this.userDataSource = userDS;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.card_technician_order, parent, false);
        }

        TextView serviceNameView = convertView.findViewById(R.id.textViewServiceName);
        TextView customerNameView = convertView.findViewById(R.id.textViewCustomerName);
        TextView addressView = convertView.findViewById(R.id.textViewAddress);
        TextView dateView = convertView.findViewById(R.id.textViewOrderDate);
        TextView statusView = convertView.findViewById(R.id.textViewStatus);

        Order order = getItem(position);

        if (order != null) {
            addressView.setText("Alamat: " + order.getAddress());
            dateView.setText("Tanggal: " + order.getOrderDate());
            statusView.setText("Status: " + order.getStatus());

            Service service = serviceDataSource.getServiceById(order.getServiceId());
            if (service != null) {
                serviceNameView.setText(service.getServiceName());
            } else {
                serviceNameView.setText("Layanan Tidak Ditemukan");
            }

            User customer = userDataSource.getUserById(order.getUserId());
            if (customer != null) {
                customerNameView.setText("Pemesan: " + customer.getUsername());
            } else {
                customerNameView.setText("Pemesan: Tidak Ditemukan");
            }
        }

        return convertView;
    }
}
