package com.example.fixit_v2.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.fixit_v2.database.DatabaseHelper;
import com.example.fixit_v2.models.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { 
        DatabaseHelper.KEY_ID, 
        DatabaseHelper.KEY_SERVICE_NAME, 
        DatabaseHelper.KEY_DESCRIPTION, 
        DatabaseHelper.KEY_PRICE, 
        DatabaseHelper.KEY_TECHNICIAN_ID, 
        DatabaseHelper.KEY_CATEGORY_ID,
        DatabaseHelper.KEY_IMAGE_PATH
    };

    public ServiceDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Service createService(String serviceName, String description, double price, int technicianId, int categoryId, String imagePath) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_SERVICE_NAME, serviceName);
        values.put(DatabaseHelper.KEY_DESCRIPTION, description);
        values.put(DatabaseHelper.KEY_PRICE, price);
        values.put(DatabaseHelper.KEY_TECHNICIAN_ID, technicianId);
        values.put(DatabaseHelper.KEY_CATEGORY_ID, categoryId);
        values.put(DatabaseHelper.KEY_IMAGE_PATH, imagePath);
        long insertId = database.insert(DatabaseHelper.TABLE_SERVICES, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVICES, allColumns, DatabaseHelper.KEY_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Service newService = cursorToService(cursor);
        cursor.close();
        return newService;
    }

    public List<Service> searchServicesByName(String keyword) {
        List<Service> services = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) {
            return services;
        }
        String selection = DatabaseHelper.KEY_SERVICE_NAME + " LIKE ?";
        String[] selectionArgs = { "%" + keyword + "%" };

        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVICES, allColumns, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            services.add(cursorToService(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return services;
    }

    // --- METHOD ADDED BACK ---
    public List<Service> getServicesByTechnician(int technicianId) {
        List<Service> services = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVICES, allColumns, 
                DatabaseHelper.KEY_TECHNICIAN_ID + " = ?", 
                new String[]{String.valueOf(technicianId)}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            services.add(cursorToService(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return services;
    }

    public List<Service> getServicesByCategory(int categoryId) {
        List<Service> services = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVICES, allColumns, 
                DatabaseHelper.KEY_CATEGORY_ID + " = ?", 
                new String[]{String.valueOf(categoryId)}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            services.add(cursorToService(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return services;
    }
    
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVICES, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Service service = cursorToService(cursor);
            services.add(service);
            cursor.moveToNext();
        }
        cursor.close();
        return services;
    }

    public Service getServiceById(int serviceId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVICES, allColumns,
                DatabaseHelper.KEY_ID + " = ?", 
                new String[]{String.valueOf(serviceId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Service service = cursorToService(cursor);
            cursor.close();
            return service;
        }
        if(cursor != null) cursor.close();
        return null;
    }

    private Service cursorToService(Cursor cursor) {
        return new Service(
            cursor.getInt(0),      // id
            cursor.getString(1),   // service_name
            cursor.getString(2),   // description
            cursor.getDouble(3),   // price
            cursor.getInt(4),      // technician_id
            cursor.getInt(5),      // category_id
            cursor.getString(6)    // image_path
        );
    }
}
