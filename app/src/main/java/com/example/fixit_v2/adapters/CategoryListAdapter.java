package com.example.fixit_v2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.List;

public class CategoryListAdapter extends ArrayAdapter<ServiceCategory> {

    private Context context;
    private TechnicianDataSource technicianDataSource;

    public CategoryListAdapter(@NonNull Context context, @NonNull List<ServiceCategory> categories, TechnicianDataSource techDS) {
        super(context, R.layout.list_item_category, categories);
        this.context = context;
        this.technicianDataSource = techDS;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_category, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.imageViewCategoryIcon);
        TextView name = convertView.findViewById(R.id.textViewCategoryName);
        TextView count = convertView.findViewById(R.id.textViewServiceCount);

        ServiceCategory category = getItem(position);

        if(category != null) {
            name.setText(category.getCategoryName());
            // This logic is flawed because technicians are not directly linked to categories anymore.
            // A more complex query would be needed. For now, we'll just show a placeholder count.
            // int technicianCount = technicianDataSource.getTechniciansByCategory(category.getId()).size(); 
            count.setText("Lihat Layanan >");

            icon.setImageResource(R.mipmap.ic_launcher);
        }

        return convertView;
    }
}
