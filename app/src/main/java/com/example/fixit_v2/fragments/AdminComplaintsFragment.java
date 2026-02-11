package com.example.fixit_v2.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixit_v2.R;
import com.example.fixit_v2.adapters.AdminComplaintAdapter;
import com.example.fixit_v2.database.DatabaseHelper;
import com.example.fixit_v2.datasource.ComplaintDataSource;
import com.example.fixit_v2.models.Complaint;

import java.util.ArrayList;
import java.util.List;

public class AdminComplaintsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminComplaintAdapter adapter;
    private List<Complaint> complaintList;
    private ComplaintDataSource complaintDataSource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_complaints, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewComplaints);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        complaintDataSource = new ComplaintDataSource(getContext());
        complaintList = new ArrayList<>();

        loadComplaints();

        adapter = new AdminComplaintAdapter(getContext(), complaintList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadComplaints() {
        complaintList.clear();
        complaintDataSource.open();
        
        // We need a method to get ALL complaints.
        // Since I can't easily modify DataSource right now and I know I only have `getComplaintByOrderId`,
        // I will use a raw query helper here or just iterate if I had a `getAllComplaints` method.
        // I should have added `getAllComplaints` to DataSource.
        // Let's check if I can add it, or if I can just use a raw cursor here for speed.
        
        // Actually, to be clean, I should add `getAllComplaints` to `ComplaintDataSource`. 
        // But to save turn count, I'll access the DB directly via helper or assume I can add the method in next step.
        // Wait, I can't use `database` from here as it's private in DataSource.
        
        // I will implement a temporary workaround using a new DataSource instance for this fragment if needed,
        // or just add the method to DataSource now. I'll Choose to add the method to DataSource.
        // But for this file I'll write the call assuming it exists or I'll use a local helper.
        
        // Let's assume I'll add `getAllComplaints()` to `ComplaintDataSource` in parallel.
        // loadComplaints logic:
        // complaintList.addAll(complaintDataSource.getAllComplaints());
        
        // Since I'm writing this file now, I'll comment out the fetch and implement it in the next step properly.
        // Or I can use a raw SQLiteOpenHelper here.
        
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_COMPLAINTS, null, null, null, null, null, DatabaseHelper.KEY_ID + " DESC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Complaint complaint = new Complaint(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ORDER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_COMPLAINT_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_COMPLAINT_PHOTO_PATH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_COMPLAINT_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_CREATED_AT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_COMPLAINT_ADMIN_COMMENT))
                );
                complaintList.add(complaint);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        
        complaintDataSource.close();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadComplaints();
        if(adapter != null) adapter.notifyDataSetChanged();
    }
}
