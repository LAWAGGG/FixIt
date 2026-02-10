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
                String imagePath = category.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        android.graphics.Bitmap bitmap = null;
                        if (imagePath.startsWith("images/")) {
                            java.io.InputStream is = context.getAssets().open(imagePath);
                            bitmap = android.graphics.BitmapFactory.decodeStream(is);
                            is.close();
                        } else {
                            java.io.File file = new java.io.File(context.getFilesDir(), imagePath);
                            if (file.exists()) {
                                bitmap = android.graphics.BitmapFactory.decodeFile(file.getAbsolutePath());
                            }
                        }

                        if (bitmap != null) {
                            binding.imageViewCategoryIcon.setImageBitmap(bitmap);
                        } else {
                            binding.imageViewCategoryIcon.setImageResource(R.drawable.ic_misc_category);
                        }
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                        binding.imageViewCategoryIcon.setImageResource(R.drawable.ic_misc_category);
                    }
                } else {
                    binding.imageViewCategoryIcon.setImageResource(R.drawable.ic_misc_category);
                }

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ServiceListActivity.class);
                    intent.putExtra("CATEGORY_ID", category.getId());
                    intent.putExtra("USER_ID", userId);
                    context.startActivity(intent);
                });
            }
        }
    }
}
