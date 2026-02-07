package com.example.fixit_v2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fixit_v2.R;
import com.example.fixit_v2.adapters.TechnicianOrderAdapter;
import com.example.fixit_v2.datasource.OrderDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.Order;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;

import java.util.List;

public class TechnicianOrdersFragment extends Fragment {

    private ListView listViewAssignedOrders;
    private OrderDataSource orderDataSource;
    private ServiceDataSource serviceDataSource;
    private UserDataSource userDataSource;
    private TechnicianDataSource technicianDataSource;
    private int technicianId;
    private List<Order> orders;
    private TechnicianOrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technician_orders, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        technicianId = preferences.getInt("technician_id", -1);

        listViewAssignedOrders = view.findViewById(R.id.listViewAssignedOrders);
        orderDataSource = new OrderDataSource(getContext());
        serviceDataSource = new ServiceDataSource(getContext());
        userDataSource = new UserDataSource(getContext());
        technicianDataSource = new TechnicianDataSource(getContext());

        registerForContextMenu(listViewAssignedOrders);

        return view;
    }

    private void loadOrders(){
        if (technicianId != -1) {
            orders = orderDataSource.getOrdersByTechnicianId(technicianId);
            adapter = new TechnicianOrderAdapter(getContext(), orders, serviceDataSource, userDataSource);
            listViewAssignedOrders.setAdapter(adapter);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Order selectedOrder = orders.get(info.position);

        if (!"Selesai".equals(selectedOrder.getStatus()) && !"Dibatalkan".equals(selectedOrder.getStatus())) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.order_status_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Order selectedOrder = orders.get(info.position);

        int itemId = item.getItemId();
        if (itemId == R.id.status_processed) {
            orderDataSource.updateOrderStatus(selectedOrder.getId(), "Diproses");
            refreshOrderList();
            return true;
        } else if (itemId == R.id.status_completed) {
            orderDataSource.updateOrderStatus(selectedOrder.getId(), "Selesai");
            Service service = serviceDataSource.getServiceById(selectedOrder.getServiceId());
            Technician technician = technicianDataSource.getTechnicianById(technicianId);
            if (service != null && technician != null) {
                double newEarnings = technician.getEarnings() + service.getPrice();
                technicianDataSource.updateTechnicianEarnings(technicianId, newEarnings);
            }
            refreshOrderList();
            return true;
        } else if (itemId == R.id.status_cancelled) {
            orderDataSource.updateOrderStatus(selectedOrder.getId(), "Dibatalkan");
            refreshOrderList();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void refreshOrderList() {
        if (adapter != null) {
            orders.clear();
            orders.addAll(orderDataSource.getOrdersByTechnicianId(technicianId));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        orderDataSource.open();
        serviceDataSource.open();
        userDataSource.open();
        technicianDataSource.open();
        loadOrders();
    }

    @Override
    public void onPause() {
        super.onPause();
        orderDataSource.close();
        serviceDataSource.close();
        userDataSource.close();
        technicianDataSource.close();
    }
}
