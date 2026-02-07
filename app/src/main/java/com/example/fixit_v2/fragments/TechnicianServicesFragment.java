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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class TechnicianServicesFragment extends Fragment {

    private Spinner spinnerCategories;
    private EditText editTextServiceName;
    private EditText editTextServiceDescription;
    private EditText editTextPrice;
    private Button buttonAddService;
    private ListView listViewMyServices;

    private ServiceCategoryDataSource categoryDataSource;
    private ServiceDataSource serviceDataSource;

    private int technicianId;
    private List<Service> myServices;
    private ArrayAdapter<Service> myServicesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technician_services, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        technicianId = preferences.getInt("technician_id", -1);

        spinnerCategories = view.findViewById(R.id.spinnerCategories);
        editTextServiceName = view.findViewById(R.id.editTextServiceName);
        editTextServiceDescription = view.findViewById(R.id.editTextServiceDescription);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        buttonAddService = view.findViewById(R.id.buttonAddService);
        listViewMyServices = view.findViewById(R.id.listViewMyServices);

        categoryDataSource = new ServiceCategoryDataSource(getContext());
        serviceDataSource = new ServiceDataSource(getContext());

        registerForContextMenu(listViewMyServices);

        buttonAddService.setOnClickListener(v -> addService());

        return view;
    }

    private void loadCategorySpinner() {
        List<ServiceCategory> allCategories = categoryDataSource.getAllServiceCategories();
        ArrayAdapter<ServiceCategory> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, allCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(categoryAdapter);
    }

    private void loadMyServices() {
        if (technicianId != -1) {
            myServices = serviceDataSource.getServicesByTechnician(technicianId);
            myServicesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, myServices);
            listViewMyServices.setAdapter(myServicesAdapter);
        }
    }

    private void addService() {
        ServiceCategory selectedCategory = (ServiceCategory) spinnerCategories.getSelectedItem();
        String serviceName = editTextServiceName.getText().toString().trim();
        String description = editTextServiceDescription.getText().toString().trim();
        String priceString = editTextPrice.getText().toString().trim();

        if (selectedCategory == null || serviceName.isEmpty() || description.isEmpty() || priceString.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceString);
            serviceDataSource.createService(serviceName, description, price, technicianId, selectedCategory.getId());
            refreshMyServicesList();
            editTextServiceName.setText("");
            editTextServiceDescription.setText("");
            editTextPrice.setText("");
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid price format.", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshMyServicesList() {
        myServices.clear();
        myServices.addAll(serviceDataSource.getServicesByTechnician(technicianId));
        myServicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.my_service_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Service selectedService = myServices.get(info.position);

        if (item.getItemId() == R.id.delete_my_service) {
            serviceDataSource.deleteService(selectedService.getId());
            refreshMyServicesList();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        categoryDataSource.open();
        serviceDataSource.open();
        loadCategorySpinner();
        loadMyServices();
    }

    @Override
    public void onPause() {
        super.onPause();
        categoryDataSource.close();
        serviceDataSource.close();
    }
}
