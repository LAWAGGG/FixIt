package com.example.fixit_v2.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.Technician;
import com.example.fixit_v2.models.User;

import java.util.List;

public class AdminTechnicianActivity extends AppCompatActivity {

    private ListView listViewTechnicians;
    private Button buttonAddTechnician;
    private TechnicianDataSource technicianDataSource;
    private UserDataSource userDataSource;
    private ArrayAdapter<Technician> adapter;
    private List<Technician> technicians;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_technician);

        listViewTechnicians = findViewById(R.id.listViewTechnicians);
        buttonAddTechnician = findViewById(R.id.buttonAddTechnician);

        technicianDataSource = new TechnicianDataSource(this);
        userDataSource = new UserDataSource(this);
        technicianDataSource.open();
        userDataSource.open();

        technicians = technicianDataSource.getAllTechnicians();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, technicians);
        listViewTechnicians.setAdapter(adapter);
        registerForContextMenu(listViewTechnicians);

        buttonAddTechnician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEditTechnicianDialog(null);
            }
        });
    }

    private void showAddEditTechnicianDialog(final Technician technician) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        Toast.makeText(AdminTechnicianActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User newUser = userDataSource.createUser(username, password, email, "Technician");
                    if (newUser != null) {
                        technicianDataSource.createTechnician(newUser.getId(), name, phone);
                    } else {
                        Toast.makeText(AdminTechnicianActivity.this, "Failed to create user. Username might already exist.", Toast.LENGTH_LONG).show();
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
        MenuInflater inflater = getMenuInflater();
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
    protected void onResume() {
        super.onResume();
        technicianDataSource.open();
        userDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        technicianDataSource.close();
        userDataSource.close();
    }
}
