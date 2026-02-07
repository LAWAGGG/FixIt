package com.example.fixit_v2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fixit_v2.R;
import com.example.fixit_v2.activities.ServiceListActivity;
import com.example.fixit_v2.adapters.CategoryListAdapter;
import com.example.fixit_v2.datasource.ServiceCategoryDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class CategoriesFragment extends Fragment {

    private ListView listViewCategories;
    private ServiceCategoryDataSource serviceCategoryDataSource;
    private TechnicianDataSource technicianDataSource; // Added
    private List<ServiceCategory> categoryList;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        listViewCategories = view.findViewById(R.id.listViewCategories);
        serviceCategoryDataSource = new ServiceCategoryDataSource(getContext());
        technicianDataSource = new TechnicianDataSource(getContext()); // Added

        return view;
    }

    private void loadCategories() {
        categoryList = serviceCategoryDataSource.getAllServiceCategories();
        // Pass the opened data source to the adapter
        CategoryListAdapter adapter = new CategoryListAdapter(getContext(), categoryList, technicianDataSource);
        listViewCategories.setAdapter(adapter);

        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServiceCategory selectedCategory = categoryList.get(position);
                Intent intent = new Intent(getActivity(), ServiceListActivity.class);
                intent.putExtra("CATEGORY_ID", selectedCategory.getId());
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceCategoryDataSource.open();
        technicianDataSource.open(); // Added
        loadCategories();
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceCategoryDataSource.close();
        technicianDataSource.close(); // Added
    }
}
