package com.example.fixit_v2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<ServiceCategory> categoryList;
    private List<ServiceCategory> categoryListFull; // Copy for filtering
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(ServiceCategory category);
        void onDeleteClick(ServiceCategory category);
    }

    public CategoryAdapter(Context context, List<ServiceCategory> categoryList, OnItemClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.categoryListFull = new java.util.ArrayList<>(categoryList); // Initialize copy
        this.listener = listener;
    }

    public void updateList(List<ServiceCategory> newList) {
        categoryList = newList;
        categoryListFull = new java.util.ArrayList<>(newList); // Update copy
        notifyDataSetChanged();
    }

    public void filter(String text) {
        categoryList.clear();
        if (text.isEmpty()) {
            categoryList.addAll(categoryListFull);
        } else {
            text = text.toLowerCase();
            for (ServiceCategory item : categoryListFull) {
                if (item.getCategoryName().toLowerCase().contains(text)) {
                    categoryList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceCategory category = categoryList.get(position);
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView imageViewMore;
        ImageView imageViewCategoryIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewCategoryName);
            imageViewMore = itemView.findViewById(R.id.imageViewMore);
            imageViewCategoryIcon = itemView.findViewById(R.id.imageViewCategoryIcon);
        }

        public void bind(final ServiceCategory category, final OnItemClickListener listener) {
            textViewName.setText(category.getCategoryName());

            // Load category icon from assets or internal storage
            String imagePath = category.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    android.graphics.Bitmap bitmap = null;
                    if (imagePath.startsWith("images/")) {
                        // Load from assets
                        java.io.InputStream is = itemView.getContext().getAssets().open(imagePath);
                        bitmap = android.graphics.BitmapFactory.decodeStream(is);
                        is.close();
                        imageViewCategoryIcon.setImageTintList(android.content.res.ColorStateList.valueOf(itemView.getContext().getColor(R.color.primary)));
                    } else {
                        // Load from internal storage
                        java.io.File file = new java.io.File(itemView.getContext().getFilesDir(), imagePath);
                        if (file.exists()) {
                            bitmap = android.graphics.BitmapFactory.decodeFile(file.getAbsolutePath());
                            imageViewCategoryIcon.setImageTintList(null); // No tint for real photos
                        }
                    }
                    
                    if (bitmap != null) {
                        imageViewCategoryIcon.setImageBitmap(bitmap);
                    } else {
                        imageViewCategoryIcon.setImageResource(R.drawable.ic_misc_category);
                        imageViewCategoryIcon.setImageTintList(android.content.res.ColorStateList.valueOf(itemView.getContext().getColor(R.color.primary)));
                    }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    imageViewCategoryIcon.setImageResource(R.drawable.ic_misc_category);
                    imageViewCategoryIcon.setImageTintList(android.content.res.ColorStateList.valueOf(itemView.getContext().getColor(R.color.primary)));
                }
            } else {
                imageViewCategoryIcon.setImageResource(R.drawable.ic_misc_category);
                imageViewCategoryIcon.setImageTintList(android.content.res.ColorStateList.valueOf(itemView.getContext().getColor(R.color.primary)));
            }

            imageViewMore.setOnClickListener(v -> {
                showPopupMenu(v, category, listener);
            });
            
            itemView.setOnClickListener(v -> listener.onEditClick(category));
        }

        private void showPopupMenu(View view, ServiceCategory category, OnItemClickListener listener) {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(view.getContext(), view);
            popup.inflate(R.menu.service_category_context_menu);
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.edit_category) {
                    listener.onEditClick(category);
                    return true;
                } else if (id == R.id.delete_category) {
                    listener.onDeleteClick(category);
                    return true;
                }
                return false;
            });
            popup.show();
        }
    }
}
