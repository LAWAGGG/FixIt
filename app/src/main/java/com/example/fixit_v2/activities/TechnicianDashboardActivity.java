package com.example.fixit_v2.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fixit_v2.R;
import com.example.fixit_v2.fragments.TechnicianOrdersFragment;
import com.example.fixit_v2.fragments.TechnicianProfileFragment;
import com.example.fixit_v2.fragments.TechnicianServicesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TechnicianDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.technician_bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.technician_fragment_container,
                    new TechnicianOrdersFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_tech_orders) {
                        selectedFragment = new TechnicianOrdersFragment();
                    } else if (itemId == R.id.nav_tech_services) {
                        selectedFragment = new TechnicianServicesFragment();
                    } else if (itemId == R.id.nav_tech_profile) {
                        selectedFragment = new TechnicianProfileFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.technician_fragment_container,
                                selectedFragment).commit();
                    }

                    return true;
                }
            };
}
