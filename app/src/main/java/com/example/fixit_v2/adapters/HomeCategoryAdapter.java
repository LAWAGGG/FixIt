package com.example.fixit_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.activities.ServiceListActivity;
import com.example.fixit_v2.activities.UserDashboardActivity;
import com.example.fixit_v2.databinding.ItemCategoryHomeBinding;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<ServiceCategory> categoryList;
    private final int userId;

    public HomeCategoryAdapter(Context context, List<ServiceCategory> categoryList, int userId) {
        this.context = context;
        this.categoryList = categoryList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemCategoryHomeBinding binding = ItemCategoryHomeBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryHomeBinding binding;

        public ViewHolder(ItemCategoryHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ServiceCategory category) {
            binding.textViewCategoryName.setText(category.getCategoryName());

            // Special "All" category
            if (category.getId() == -99) {
                binding.imageViewCategoryIcon.setImageResource(android.R.drawable.ic_menu_sort_by_size);
                itemView.setOnClickListener(v -> {
                    if (context instanceof UserDashboardActivity) {
                        ((UserDashboardActivity) context).navigateToCategories();
                    }
                });
            } else {
                int iconResId = getIconForCategory(category.getCategoryName());
                binding.imageViewCategoryIcon.setImageDrawable(ContextCompat.getDrawable(context, iconResId));
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ServiceListActivity.class);
                    intent.putExtra("CATEGORY_ID", category.getId());
                    intent.putExtra("USER_ID", userId);
                    context.startActivity(intent);
                });
            }
        }

        private int getIconForCategory(String categoryName) {
            String lowerCaseName = categoryName.toLowerCase();
            if (lowerCaseName.contains("cleaning")) return R.drawable.ic_cleaning;
            if (lowerCaseName.contains("plumbing")) return R.drawable.ic_plumbing;
            if (lowerCaseName.contains("electric")) return R.drawable.ic_electric;
            if (lowerCaseName.contains("paint")) return R.drawable.ic_painting;
            if (lowerCaseName.contains("ac")) return R.drawable.ic_ac_repair;
            return R.drawable.ic_misc_category; // Default icon
        }
    }
}
