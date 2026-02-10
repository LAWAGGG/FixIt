package com.example.fixit_v2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fixit_v2.adapters.UserCategoryAdapter;
import com.example.fixit_v2.databinding.FragmentCategoriesBinding;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;
    private ServiceCategoryDataSource serviceCategoryDataSource;
    private ServiceDataSource serviceDataSource;
    private List<ServiceCategory> categoryList;
    private UserCategoryAdapter adapter;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        binding.recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        serviceCategoryDataSource = new ServiceCategoryDataSource(getContext());
        serviceDataSource = new ServiceDataSource(getContext());

        setupSearch();

        return binding.getRoot();
    }

    private void setupSearch() {
        binding.btnSearch.setOnClickListener(v -> {
            binding.textViewTitle.setVisibility(View.GONE);
            binding.btnSearch.setVisibility(View.GONE);
            binding.searchContainer.setVisibility(View.VISIBLE);
            binding.editTextSearch.requestFocus();
            showKeyboard(binding.editTextSearch);
        });

        binding.btnCloseSearch.setOnClickListener(v -> {
            binding.textViewTitle.setVisibility(View.VISIBLE);
            binding.btnSearch.setVisibility(View.VISIBLE);
            binding.searchContainer.setVisibility(View.GONE);
            binding.editTextSearch.setText("");
            hideKeyboard(binding.editTextSearch);
        });

        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterCategories(String query) {
        if (categoryList == null || adapter == null) return;

        List<ServiceCategory> filteredList = new ArrayList<>();
        for (ServiceCategory category : categoryList) {
            if (category.getCategoryName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(category);
            }
        }
        adapter.updateList(filteredList);
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadCategories() {
        categoryList = serviceCategoryDataSource.getAllServiceCategories();
        adapter = new UserCategoryAdapter(getContext(), categoryList, serviceDataSource);
        binding.recyclerViewCategories.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceCategoryDataSource.open();
        serviceDataSource.open();
        loadCategories();
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceCategoryDataSource.close();
        serviceDataSource.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
