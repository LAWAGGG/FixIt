package com.example.fixit_v2.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.adapters.TechnicianAdapter;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.Technician;
import com.example.fixit_v2.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdminTechniciansFragment extends Fragment {

    private RecyclerView recyclerViewTechnicians;
    private FloatingActionButton fabAddTechnician;
    private TechnicianDataSource technicianDataSource;
    private UserDataSource userDataSource;
    private TechnicianAdapter adapter;
    private List<Technician> technicians;

    private android.widget.EditText editTextSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_technicians, container, false);

        recyclerViewTechnicians = view.findViewById(R.id.recyclerViewTechnicians);
        fabAddTechnician = view.findViewById(R.id.fabAddTechnician);
        editTextSearch = view.findViewById(R.id.editTextSearch);

        recyclerViewTechnicians.setLayoutManager(new LinearLayoutManager(getContext()));

        technicianDataSource = new TechnicianDataSource(getContext());
        userDataSource = new UserDataSource(getContext());
        technicianDataSource.open();
        userDataSource.open();

        technicians = technicianDataSource.getAllTechnicians();

        adapter = new TechnicianAdapter(getContext(), technicians, new TechnicianAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Technician technician) {
                showAddEditTechnicianDialog(technician);
            }

            @Override
            public void onDeleteClick(Technician technician) {
                showDeleteConfirmationDialog(technician);
            }
        });
        recyclerViewTechnicians.setAdapter(adapter);

        fabAddTechnician.setOnClickListener(v -> showAddEditTechnicianDialog(null));

        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        return view;
    }

    private void showDeleteConfirmationDialog(Technician technician) {
        new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                .setTitle("Delete Technician")
                .setMessage("Are you sure you want to delete " + technician.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    technicianDataSource.deleteTechnician(technician);
                    refreshTechnicianList();
                    Toast.makeText(getContext(), "Technician deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddEditTechnicianDialog(final Technician technician) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_technician, null);
        builder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final EditText editTextPhone = dialogView.findViewById(R.id.editTextPhone);
        final EditText editTextUsername = dialogView.findViewById(R.id.editTextUsername);
        final EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        final EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);
        android.widget.TextView textViewTitle = dialogView.findViewById(R.id.textViewDialogTitle);
        android.widget.Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        android.widget.Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        textViewTitle.setText(technician == null ? "Add Technician" : "Edit Technician");
        buttonSave.setText(technician == null ? "Add" : "Save");

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

        AlertDialog dialog = builder.create();
        buttonSave.setOnClickListener(v -> {
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
                    Toast.makeText(getContext(), "Technician added successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to create user. Username might already exist.", Toast.LENGTH_LONG).show();
                }
            } else {
                if (name.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                technician.setName(name);
                technician.setPhoneNumber(phone);
                technicianDataSource.updateTechnician(technician);
                Toast.makeText(getContext(), "Technician updated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            refreshTechnicianList();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.gravity = android.view.Gravity.CENTER;
            params.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
            params.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            params.dimAmount = 0.8f;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
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
