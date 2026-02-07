package com.example.fixit_v2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fixit_v2.R;
import com.example.fixit_v2.activities.LoginActivity;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Technician;

public class TechnicianProfileFragment extends Fragment {

    private TextView textViewTechnicianName;
    private TextView textViewTechnicianEarnings;
    private Button buttonLogout;
    private TechnicianDataSource technicianDataSource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technician_profile, container, false);

        textViewTechnicianName = view.findViewById(R.id.textViewTechnicianName);
        textViewTechnicianEarnings = view.findViewById(R.id.textViewTechnicianEarnings);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        technicianDataSource = new TechnicianDataSource(getContext());

        buttonLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void displayTechnicianInfo(){
        technicianDataSource.open();
        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int technicianId = preferences.getInt("technician_id", -1);
        
        if (technicianId != -1) {
            Technician currentTechnician = technicianDataSource.getTechnicianById(technicianId);
            if (currentTechnician != null) {
                textViewTechnicianName.setText(currentTechnician.getName());
                textViewTechnicianEarnings.setText("Total Pendapatan: Rp " + String.format("%.2f", currentTechnician.getEarnings()));
            }
        }
        technicianDataSource.close();
    }

    private void logout() {
        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();

        Toast.makeText(getContext(), "You have been logged out.", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayTechnicianInfo();
    }
}
