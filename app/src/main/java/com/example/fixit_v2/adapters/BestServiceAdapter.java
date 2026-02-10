package com.example.fixit_v2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.activities.ServiceDetailActivity;
import com.example.fixit_v2.databinding.ItemServiceCardBinding;
import com.example.fixit_v2.datasource.ReviewDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BestServiceAdapter extends RecyclerView.Adapter<BestServiceAdapter.ViewHolder> {

    private final Context context;
    private List<Service> serviceList;
    private final List<Service> serviceListFull; // A copy of the original list
    private final TechnicianDataSource technicianDataSource;
    private final ReviewDataSource reviewDataSource;
    private final com.example.fixit_v2.datasource.ServiceCategoryDataSource categoryDataSource;

    public BestServiceAdapter(Context context, List<Service> serviceList, TechnicianDataSource technicianDataSource, ReviewDataSource reviewDataSource, com.example.fixit_v2.datasource.ServiceCategoryDataSource categoryDataSource) {
        this.context = context;
        this.serviceList = new ArrayList<>(serviceList);
        this.serviceListFull = new ArrayList<>(serviceList); // Keep a copy of the full list
        this.technicianDataSource = technicianDataSource;
        this.reviewDataSource = reviewDataSource;
        this.categoryDataSource = categoryDataSource;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemServiceCardBinding binding = ItemServiceCardBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(serviceList.get(position));
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    // --- FILTER METHOD ---
    public void filterList(List<Service> filteredList) {
        serviceList = filteredList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemServiceCardBinding binding;

        public ViewHolder(ItemServiceCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Service clickedService = serviceList.get(position);
                    Intent intent = new Intent(context, ServiceDetailActivity.class);
                    intent.putExtra("SERVICE_ID", clickedService.getId());
                    context.startActivity(intent);
                }
            });
        }

        void bind(Service service) {
            binding.textViewServiceName.setText(service.getServiceName());
            binding.textViewDiscountPrice.setText(String.format(Locale.GERMAN, "Rp %,d", (long) service.getPrice()));

            // Load service image
            String imagePath = service.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    android.graphics.Bitmap bitmap = null;
                    
                    // Check if image is from assets or internal storage
                    if (imagePath.startsWith("images/")) {
                        // Load from assets
                        java.io.InputStream inputStream = context.getAssets().open(imagePath);
                        bitmap = android.graphics.BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                    } else {
                        // Load from internal storage
                        java.io.File imageFile = new java.io.File(context.getFilesDir(), imagePath);
                        if (imageFile.exists()) {
                            bitmap = android.graphics.BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        }
                    }
                    
                    if (bitmap != null) {
                        binding.imageViewService.setImageBitmap(bitmap);
                    } else {
                        binding.imageViewService.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                } catch (java.io.IOException e) {
                    // If image not found, use placeholder
                    binding.imageViewService.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                // No image path, use placeholder
                binding.imageViewService.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            float averageRating = reviewDataSource.getAverageRating(service.getId());
            if (averageRating > 0) {
                binding.textViewRating.setText(String.format(Locale.US, "%.1f", averageRating));
            } else {
                binding.textViewRating.setText("New");
            }

            Technician technician = technicianDataSource.getTechnicianById(service.getTechnicianId());
            if (technician != null) {
                binding.textViewProviderName.setText(technician.getName());
            }

            if (categoryDataSource != null) {
                com.example.fixit_v2.models.ServiceCategory category = categoryDataSource.getServiceCategoryById(service.getCategoryId());
                binding.textViewCategoryName.setText(category != null ? category.getCategoryName() : "Unknown Category");
            }
        }
    }
}
