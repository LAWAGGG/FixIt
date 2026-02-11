package com.example.fixit_v2.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.fixit_v2.database.DatabaseHelper;
import com.example.fixit_v2.models.Complaint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ComplaintDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {
            DatabaseHelper.KEY_ID,
            DatabaseHelper.KEY_ORDER_ID,
            DatabaseHelper.KEY_COMPLAINT_DESCRIPTION,
            DatabaseHelper.KEY_COMPLAINT_PHOTO_PATH,
            DatabaseHelper.KEY_COMPLAINT_STATUS,
            DatabaseHelper.KEY_CREATED_AT
    };

    public ComplaintDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        dbHelper.close();
    }

    public Complaint createComplaint(int orderId, String description, String photoPath) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_ORDER_ID, orderId);
        values.put(DatabaseHelper.KEY_COMPLAINT_DESCRIPTION, description);
        values.put(DatabaseHelper.KEY_COMPLAINT_PHOTO_PATH, photoPath);
        values.put(DatabaseHelper.KEY_COMPLAINT_STATUS, "Pending");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String createdAt = sdf.format(new Date());
        values.put(DatabaseHelper.KEY_CREATED_AT, createdAt);

        long insertId = database.insert(DatabaseHelper.TABLE_COMPLAINTS, null, values);
        
        // Return dummy object or query if needed, here just constructing assuming success
        return new Complaint((int) insertId, orderId, description, photoPath, "Pending", createdAt);
    }

    public Complaint getComplaintByOrderId(int orderId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_COMPLAINTS, allColumns,
                DatabaseHelper.KEY_ORDER_ID + " = " + orderId, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Complaint complaint = cursorToComplaint(cursor);
            cursor.close();
            return complaint;
        }
        if (cursor != null) cursor.close();
        if (cursor != null) cursor.close();
        return null; // No complaint found
    }

    public int updateComplaintStatus(int complaintId, String newStatus) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_COMPLAINT_STATUS, newStatus);
        return database.update(DatabaseHelper.TABLE_COMPLAINTS, values, DatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(complaintId)});
    }

    private Complaint cursorToComplaint(Cursor cursor) {
        return new Complaint(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5)
        );
    }
}
