package com.example.fixit_v2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class CategoryGridAdapter extends BaseAdapter {
    private Context context;
    private List<ServiceCategory> categories;

    // A special ID for the "All" button
    public static final int ALL_CATEGORIES_ID = -99;

    public CategoryGridAdapter(Context context, List<ServiceCategory> categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item_category, null);
        }

        ImageView imageView = convertView.findViewById(R.id.imageViewCategoryIcon);
        TextView textView = convertView.findViewById(R.id.textViewCategoryName);

        ServiceCategory category = categories.get(position);
        textView.setText(category.getCategoryName());

        if (category.getId() == ALL_CATEGORIES_ID) {
            // Special item for "All"
            imageView.setImageResource(android.R.drawable.ic_menu_sort_by_size); // Placeholder icon for "All"
        } else {
            // TODO: Set category-specific icons here based on category name or ID
            imageView.setImageResource(R.mipmap.ic_launcher); // Placeholder for regular categories
        }

        return convertView;
    }
}
