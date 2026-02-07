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
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.User;

public class ProfileFragment extends Fragment {

    private TextView textViewUsername;
    private Button buttonLogout;
    private UserDataSource userDataSource;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        textViewUsername = view.findViewById(R.id.textViewUsername);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        userDataSource = new UserDataSource(getContext());

        buttonLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void displayUserInfo(){
        userDataSource.open();
        User currentUser = userDataSource.getUserById(userId);
        if (currentUser != null) {
            textViewUsername.setText("Username: " + currentUser.getUsername());
        }
        userDataSource.close();
    }

    private void logout() {
        // Clear SharedPreferences
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
        displayUserInfo();
    }
}
