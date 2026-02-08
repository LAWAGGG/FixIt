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

    public BestServiceAdapter(Context context, List<Service> serviceList, TechnicianDataSource technicianDataSource, ReviewDataSource reviewDataSource) {
        this.context = context;
        this.serviceList = new ArrayList<>(serviceList);
        this.serviceListFull = new ArrayList<>(serviceList); // Keep a copy of the full list
        this.technicianDataSource = technicianDataSource;
        this.reviewDataSource = reviewDataSource;
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
        }
    }
}
