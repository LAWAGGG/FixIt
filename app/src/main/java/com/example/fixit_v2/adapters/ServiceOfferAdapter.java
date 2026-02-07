package com.example.fixit_v2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fixit_v2.R;
import com.example.fixit_v2.datasource.ReviewDataSource;
import com.example.fixit_v2.datasource.TechnicianDataSource;
import com.example.fixit_v2.models.Service;
import com.example.fixit_v2.models.Technician;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ServiceOfferAdapter extends ArrayAdapter<Service> {

    private Context context;
    private TechnicianDataSource technicianDataSource;
    private ReviewDataSource reviewDataSource;

    public ServiceOfferAdapter(@NonNull Context context, @NonNull List<Service> services, TechnicianDataSource techDS, ReviewDataSource reviewDS) {
        super(context, R.layout.card_best_service, services);
        this.context = context;
        this.technicianDataSource = techDS;
        this.reviewDataSource = reviewDS;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.card_best_service, parent, false);
        }

        Service service = getItem(position);

        if (service != null) {
            TextView serviceName = convertView.findViewById(R.id.textViewServiceName);
            TextView servicePrice = convertView.findViewById(R.id.textViewServicePrice);
            TextView techName = convertView.findViewById(R.id.textViewTechnicianName);
            RatingBar ratingBar = convertView.findViewById(R.id.ratingBarService);
            ImageView techImage = convertView.findViewById(R.id.imageViewTechnician);

            serviceName.setText(service.getServiceName());

            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            format.setMaximumFractionDigits(0);
            servicePrice.setText(format.format(service.getPrice()));

            Technician technician = technicianDataSource.getTechnicianById(service.getTechnicianId());
            techName.setText(technician != null ? technician.getName() : "Unknown");

            float averageRating = reviewDataSource.getAverageRating(service.getId());
            ratingBar.setRating(averageRating);
            
            techImage.setImageResource(R.mipmap.ic_launcher_round);
        }

        return convertView;
    }
}
