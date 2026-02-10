package com.example.fixit_v2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fixit_v2.R;
import com.example.fixit_v2.activities.LoginActivity;
import com.example.fixit_v2.databinding.FragmentProfileBinding;
import com.example.fixit_v2.databinding.ItemProfileMenuBinding;
import com.example.fixit_v2.datasource.UserDataSource;
import com.example.fixit_v2.models.User;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserDataSource userDataSource;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);
        userDataSource = new UserDataSource(getContext());

        setupMenu();
        return binding.getRoot();
    }

    private void displayUserInfo() {
        userDataSource.open();
        User currentUser = userDataSource.getUserById(userId);
        if (currentUser != null) {
            binding.textViewUsername.setText(currentUser.getUsername());
            // Here you would load the user's avatar image into binding.imageViewAvatar
            // using a library like Glide or Picasso.
        }
        userDataSource.close();
    }

    private void setupMenu() {
        // Setup menu item texts and icons
        setupMenuItem(binding.menuLogout, R.drawable.ic_logout, "Logout");

        // Setup click listeners
        binding.menuLogout.getRoot().setOnClickListener(v -> logout());
    }

    private void setupMenuItem(ItemProfileMenuBinding menuBinding, int iconRes, String title) {
        menuBinding.imageViewMenuIcon.setImageResource(iconRes);
        menuBinding.textViewMenuTitle.setText(title);
    }

    private void logout() {
        // Clear SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();

        Toast.makeText(getContext(), "You have been logged out.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
    
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayUserInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
