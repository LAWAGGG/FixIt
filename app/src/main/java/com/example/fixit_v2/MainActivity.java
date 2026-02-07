package com.example.fixit_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit_v2.activities.AdminDashboardActivity;
import com.example.fixit_v2.activities.LoginActivity;
import com.example.fixit_v2.activities.TechnicianDashboardActivity;
import com.example.fixit_v2.activities.UserDashboardActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for a saved user session
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        int userId = preferences.getInt("user_id", -1);
        String role = preferences.getString("role", null);

        Intent intent;
        if (userId != -1 && role != null) {
            // User is logged in, redirect to the correct dashboard
            switch (role) {
                case "Admin":
                    intent = new Intent(this, AdminDashboardActivity.class);
                    break;
                case "User":
                    intent = new Intent(this, UserDashboardActivity.class);
                    break;
                case "Technician":
                    intent = new Intent(this, TechnicianDashboardActivity.class);
                    // Pass technician_id which should have been saved during login
                    int technicianId = preferences.getInt("technician_id", -1);
                    intent.putExtra("TECHNICIAN_ID", technicianId);
                    break;
                default:
                    // If role is unknown, default to login
                    intent = new Intent(this, LoginActivity.class);
                    break;
            }
        } else {
            // No session found, go to login
            intent = new Intent(this, LoginActivity.class);
        }

        // Clear all previous activities
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
