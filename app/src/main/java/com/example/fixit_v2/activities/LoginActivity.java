package com.example.fixit_v2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.Technician;
import com.example.fixit_v2.models.User;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private UserDataSource userDataSource;
    private TechnicianDataSource technicianDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        userDataSource = new UserDataSource(this);
        technicianDataSource = new TechnicianDataSource(this);

        buttonLogin.setOnClickListener(v -> loginUser());

        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        userDataSource.open();
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        User user = userDataSource.login(username, password);
        userDataSource.close(); // Close immediately after use

        if (user != null) {
            SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("user_id", user.getId());
            editor.putString("role", user.getRole());

            if ("Technician".equals(user.getRole())) {
                technicianDataSource.open(); // Open only when needed
                Technician technician = technicianDataSource.getTechnicianByUserId(user.getId());
                if (technician != null) {
                    editor.putInt("technician_id", technician.getId());
                }
                technicianDataSource.close(); // Close immediately after use
            }
            editor.apply();

            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            redirectToDashboard(user);
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToDashboard(User user) {
        Intent intent = null;
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        switch (user.getRole()) {
            case "Admin":
                intent = new Intent(this, AdminDashboardActivity.class);
                break;
            case "User":
                intent = new Intent(this, UserDashboardActivity.class);
                intent.putExtra("USER_ID", user.getId());
                break;
            case "Technician":
                int technicianId = preferences.getInt("technician_id", -1);
                if (technicianId != -1) {
                    intent = new Intent(this, TechnicianDashboardActivity.class);
                    intent.putExtra("TECHNICIAN_ID", technicianId);
                } else {
                    Toast.makeText(this, "Could not find technician profile.", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            default:
                Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show();
                return;
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
