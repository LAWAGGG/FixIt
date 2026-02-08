package com.example.fixit_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.activities.ServiceListActivity;
import com.example.fixit_v2.databinding.ListItemCategoryBinding;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<ServiceCategory> categoryList;
    private final int userId;

    public CategoryAdapter(Context context, List<ServiceCategory> categoryList, int userId) {
        this.context = context;
        this.categoryList = categoryList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ListItemCategoryBinding binding = ListItemCategoryBinding.inflate(inflater, parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ServiceCategory category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ListItemCategoryBinding binding;

        public CategoryViewHolder(ListItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ServiceCategory selectedCategory = categoryList.get(position);
                    Intent intent = new Intent(context, ServiceListActivity.class);
                    intent.putExtra("CATEGORY_ID", selectedCategory.getId());
                    intent.putExtra("USER_ID", userId);
                    context.startActivity(intent);
                }
            });
        }

        void bind(ServiceCategory category) {
            binding.textViewCategoryName.setText(category.getCategoryName());

            int iconResId;
            String categoryName = category.getCategoryName().toLowerCase();
            
            // Simple logic to assign icons based on category name
            if (categoryName.contains("cleaning")) {
                iconResId = R.drawable.ic_cleaning; // Assuming you have these drawables
            } else if (categoryName.contains("plumbing")) {
                iconResId = R.drawable.ic_plumbing;
            } else if (categoryName.contains("electric")) {
                iconResId = R.drawable.ic_electric;
            } else if (categoryName.contains("paint")) {
                iconResId = R.drawable.ic_painting;
            } else if (categoryName.contains("ac")) {
                iconResId = R.drawable.ic_ac_repair;
            } else {
                iconResId = R.drawable.ic_misc_category; // A default icon
            }
            
            binding.imageViewCategoryIcon.setImageDrawable(ContextCompat.getDrawable(context, iconResId));
            
            // Example for service count - you can add a method to your model for this
            binding.textViewServiceCount.setText("10+ Services");
        }
    }
}
