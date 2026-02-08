package com.example.fixit_v2.fragments;

import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.Technician;
import com.example.fixit_v2.models.User;

import java.util.List;

public class AdminTechniciansFragment extends Fragment {

    private ListView listViewTechnicians;
    private Button buttonAddTechnician;
    private TechnicianDataSource technicianDataSource;
    private UserDataSource userDataSource;
    private ArrayAdapter<Technician> adapter;
    private List<Technician> technicians;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_technicians, container, false);

        listViewTechnicians = view.findViewById(R.id.listViewTechnicians);
        buttonAddTechnician = view.findViewById(R.id.buttonAddTechnician);

        technicianDataSource = new TechnicianDataSource(getContext());
        userDataSource = new UserDataSource(getContext());
        technicianDataSource.open();
        userDataSource.open();

        technicians = technicianDataSource.getAllTechnicians();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, technicians);
        listViewTechnicians.setAdapter(adapter);
        registerForContextMenu(listViewTechnicians);

        buttonAddTechnician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEditTechnicianDialog(null);
            }
        });

        return view;
    }

    private void showAddEditTechnicianDialog(final Technician technician) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_technician, null);
        builder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final EditText editTextPhone = dialogView.findViewById(R.id.editTextPhone);
        final EditText editTextUsername = dialogView.findViewById(R.id.editTextUsername);
        final EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        final EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);

        builder.setTitle(technician == null ? "Add Technician" : "Edit Technician");

        if (technician != null) {
            editTextName.setText(technician.getName());
            editTextPhone.setText(technician.getPhoneNumber());
            editTextUsername.setVisibility(View.GONE);
            editTextEmail.setVisibility(View.GONE);
            editTextPassword.setVisibility(View.GONE);
        } else {
            editTextUsername.setVisibility(View.VISIBLE);
            editTextEmail.setVisibility(View.VISIBLE);
            editTextPassword.setVisibility(View.VISIBLE);
        }

        builder.setPositiveButton(technician == null ? "Add" : "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextName.getText().toString();
                String phone = editTextPhone.getText().toString();

                if (technician == null) {
                    String username = editTextUsername.getText().toString();
                    String email = editTextEmail.getText().toString();
                    String password = editTextPassword.getText().toString();

                    if (name.isEmpty() || phone.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()){
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User newUser = userDataSource.createUser(username, password, email, phone, "Technician");
                    if (newUser != null) {
                        technicianDataSource.createTechnician(newUser.getId(), name, phone);
                    } else {
                        Toast.makeText(getContext(), "Failed to create user. Username might already exist.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    technician.setName(name);
                    technician.setPhoneNumber(phone);
                    technicianDataSource.updateTechnician(technician);
                }
                refreshTechnicianList();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.technician_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Technician selectedTechnician = technicians.get(info.position);

        int itemId = item.getItemId();
        if (itemId == R.id.edit_technician) {
            showAddEditTechnicianDialog(selectedTechnician);
            return true;
        } else if (itemId == R.id.delete_technician) {
            technicianDataSource.deleteTechnician(selectedTechnician);
            refreshTechnicianList();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void refreshTechnicianList() {
        technicians.clear();
        technicians.addAll(technicianDataSource.getAllTechnicians());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        technicianDataSource.open();
        userDataSource.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        technicianDataSource.close();
        userDataSource.close();
    }
}
