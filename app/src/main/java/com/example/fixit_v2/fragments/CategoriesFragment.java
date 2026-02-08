package com.example.fixit_v2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fixit_v2.adapters.CategoryAdapter;
import com.example.fixit_v2.databinding.FragmentCategoriesBinding;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;
    private ServiceCategoryDataSource serviceCategoryDataSource;
    private List<ServiceCategory> categoryList;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        binding.recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        serviceCategoryDataSource = new ServiceCategoryDataSource(getContext());

        return binding.getRoot();
    }

    private void loadCategories() {
        categoryList = serviceCategoryDataSource.getAllServiceCategories();
        CategoryAdapter adapter = new CategoryAdapter(getContext(), categoryList, userId);
        binding.recyclerViewCategories.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceCategoryDataSource.open();
        loadCategories();
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceCategoryDataSource.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
