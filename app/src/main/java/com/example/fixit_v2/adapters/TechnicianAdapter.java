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
import com.example.fixit_v2.models.Technician;

import java.util.List;

public class TechnicianAdapter extends RecyclerView.Adapter<TechnicianAdapter.ViewHolder> {

    private Context context;
    private List<Technician> technicianList;
    private List<Technician> technicianListFull; // Copy for filtering
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Technician technician);
        void onDeleteClick(Technician technician);
    }

    public TechnicianAdapter(Context context, List<Technician> technicianList, OnItemClickListener listener) {
        this.context = context;
        this.technicianList = technicianList;
        this.technicianListFull = new java.util.ArrayList<>(technicianList); // Initialize copy
        this.listener = listener;
    }

    public void updateList(List<Technician> newList) {
        technicianList = newList;
        technicianListFull = new java.util.ArrayList<>(newList); // Update copy
        notifyDataSetChanged();
    }

    public void filter(String text) {
        technicianList.clear();
        if (text.isEmpty()) {
            technicianList.addAll(technicianListFull);
        } else {
            text = text.toLowerCase();
            for (Technician item : technicianListFull) {
                if (item.getName().toLowerCase().contains(text) || item.getPhoneNumber().contains(text)) {
                    technicianList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_technician_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Technician technician = technicianList.get(position);
        holder.bind(technician, listener);
    }

    @Override
    public int getItemCount() {
        return technicianList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewPhone;
        ImageView imageViewMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            imageViewMore = itemView.findViewById(R.id.imageViewMore);
        }

        public void bind(final Technician technician, final OnItemClickListener listener) {
            textViewName.setText(technician.getName());
            textViewPhone.setText(technician.getPhoneNumber());

            imageViewMore.setOnClickListener(v -> {
                // Show popup menu or simply trigger edit for now, 
                // but usually "more" implies options. 
                // For simplicity matching previous context menu logic, let's show a popup menu.
                showPopupMenu(v, technician, listener);
            });
            
            // Allow clicking the card to edit as well or just stick to the more button
            itemView.setOnClickListener(v -> listener.onEditClick(technician));
        }

        private void showPopupMenu(View view, Technician technician, OnItemClickListener listener) {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(view.getContext(), view);
            popup.inflate(R.menu.technician_context_menu); // Reuse existing menu
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.edit_technician) {
                    listener.onEditClick(technician);
                    return true;
                } else if (id == R.id.delete_technician) {
                    listener.onDeleteClick(technician);
                    return true;
                }
                return false;
            });
            popup.show();
        }
    }
}
