package com.example.fixit_v2.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.fixit_v2.database.DatabaseHelper;
import com.example.fixit_v2.models.ServiceCategory;

import java.util.ArrayList;
import java.util.List;

public class ServiceCategoryDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { DatabaseHelper.KEY_ID, DatabaseHelper.KEY_CATEGORY_NAME };

    public ServiceCategoryDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ServiceCategory createServiceCategory(String categoryName) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_CATEGORY_NAME, categoryName);
        long insertId = database.insert(DatabaseHelper.TABLE_SERVICE_CATEGORIES, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVICE_CATEGORIES, allColumns, DatabaseHelper.KEY_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        ServiceCategory newCategory = cursorToServiceCategory(cursor);
        cursor.close();
        return newCategory;
    }

    public int updateServiceCategory(ServiceCategory category) {
        long id = category.getId();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_CATEGORY_NAME, category.getCategoryName());
        return database.update(DatabaseHelper.TABLE_SERVICE_CATEGORIES, values, DatabaseHelper.KEY_ID + " = " + id, null);
    }

    public void deleteServiceCategory(ServiceCategory category) {
        long id = category.getId();
        database.delete(DatabaseHelper.TABLE_SERVICE_CATEGORIES, DatabaseHelper.KEY_ID + " = " + id, null);
    }

    public List<ServiceCategory> getAllServiceCategories() {
        List<ServiceCategory> categories = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVICE_CATEGORIES, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ServiceCategory category = cursorToServiceCategory(cursor);
            categories.add(category);
            cursor.moveToNext();
        }
        cursor.close();
        return categories;
    }

    public ServiceCategory getServiceCategoryById(int categoryId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVICE_CATEGORIES, allColumns, DatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(categoryId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            ServiceCategory category = cursorToServiceCategory(cursor);
            cursor.close();
            return category;
        } 
        if(cursor != null) cursor.close();
        return null;
    }

    private ServiceCategory cursorToServiceCategory(Cursor cursor) {
        return new ServiceCategory(cursor.getInt(0), cursor.getString(1));
    }
}
