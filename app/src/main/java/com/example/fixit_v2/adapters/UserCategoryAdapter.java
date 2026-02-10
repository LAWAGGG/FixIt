package com.example.fixit_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.activities.ServiceListActivity;
import com.example.fixit_v2.datasource.ServiceDataSource;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class UserCategoryAdapter extends RecyclerView.Adapter<UserCategoryAdapter.ViewHolder> {

    private Context context;
    private List<ServiceCategory> categoryList;
    private ServiceDataSource serviceDataSource;

    public UserCategoryAdapter(Context context, List<ServiceCategory> categoryList, ServiceDataSource serviceDataSource) {
        this.context = context;
        this.categoryList = categoryList;
        this.serviceDataSource = serviceDataSource;
    }

    public void updateList(List<ServiceCategory> newList) {
        this.categoryList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceCategory category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategoryName;
        TextView textViewServiceCount;
        ImageView imageViewCategoryIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            textViewServiceCount = itemView.findViewById(R.id.textViewServiceCount);
            imageViewCategoryIcon = itemView.findViewById(R.id.imageViewCategoryIcon);
        }

        void bind(final ServiceCategory category) {
            textViewCategoryName.setText(category.getCategoryName());
            
            // Get service count for this category
            int serviceCount = serviceDataSource.getServicesByCategory(category.getId()).size();
            textViewServiceCount.setText(serviceCount + " Service" + (serviceCount != 1 ? "s" : ""));

            // Load category icon from assets or internal storage
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
                        imageViewCategoryIcon.setImageBitmap(bitmap);
                    } else {
                        imageViewCategoryIcon.setImageResource(R.drawable.ic_misc_category);
                    }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    imageViewCategoryIcon.setImageResource(R.drawable.ic_misc_category);
                }
            } else {
                imageViewCategoryIcon.setImageResource(R.drawable.ic_misc_category);
            }

            // Set click listener to navigate to ServiceListActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ServiceListActivity.class);
                intent.putExtra("CATEGORY_ID", category.getId());
                context.startActivity(intent);
            });
        }
    }
}
