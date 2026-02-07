package com.example.fixit_v2.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.fixit_v2.database.DatabaseHelper;
import com.example.fixit_v2.models.Technician;

import java.util.ArrayList;
import java.util.List;

public class TechnicianDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { DatabaseHelper.KEY_ID, DatabaseHelper.KEY_USER_ID, DatabaseHelper.KEY_NAME, DatabaseHelper.KEY_PHONE_NUMBER, DatabaseHelper.KEY_EARNINGS };

    public TechnicianDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Technician createTechnician(int userId, String name, String phoneNumber) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USER_ID, userId);
        values.put(DatabaseHelper.KEY_NAME, name);
        values.put(DatabaseHelper.KEY_PHONE_NUMBER, phoneNumber);
        values.put(DatabaseHelper.KEY_EARNINGS, 0);
        long insertId = database.insert(DatabaseHelper.TABLE_TECHNICIANS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_TECHNICIANS, allColumns, DatabaseHelper.KEY_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Technician newTechnician = cursorToTechnician(cursor);
        cursor.close();
        return newTechnician;
    }

    public int updateTechnician(Technician technician) {
        long id = technician.getId();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USER_ID, technician.getUserId());
        values.put(DatabaseHelper.KEY_NAME, technician.getName());
        values.put(DatabaseHelper.KEY_PHONE_NUMBER, technician.getPhoneNumber());
        values.put(DatabaseHelper.KEY_EARNINGS, technician.getEarnings());
        return database.update(DatabaseHelper.TABLE_TECHNICIANS, values, DatabaseHelper.KEY_ID + " = " + id, null);
    }

    public int updateTechnicianEarnings(int technicianId, double earnings) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_EARNINGS, earnings);
        return database.update(DatabaseHelper.TABLE_TECHNICIANS, values, DatabaseHelper.KEY_ID + " = " + technicianId, null);
    }

    public void deleteTechnician(Technician technician) {
        long id = technician.getId();
        database.delete(DatabaseHelper.TABLE_TECHNICIANS, DatabaseHelper.KEY_ID + " = " + id, null);
    }

    public List<Technician> getAllTechnicians() {
        List<Technician> technicians = new ArrayList<Technician>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_TECHNICIANS, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Technician technician = cursorToTechnician(cursor);
            technicians.add(technician);
            cursor.moveToNext();
        }
        cursor.close();
        return technicians;
    }
    
    public Technician getTechnicianById(int technicianId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_TECHNICIANS, allColumns, DatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(technicianId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Technician technician = cursorToTechnician(cursor);
            cursor.close();
            return technician;
        }
        if(cursor != null) cursor.close();
        return null;
    }

    public Technician getTechnicianByUserId(int userId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_TECHNICIANS, allColumns, DatabaseHelper.KEY_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Technician technician = cursorToTechnician(cursor);
            cursor.close();
            return technician;
        }
        if(cursor != null) cursor.close();
        return null;
    }

    public List<Technician> getTechniciansByCategory(int categoryId) {
        List<Technician> technicians = new ArrayList<>();
        String query = "SELECT DISTINCT t.* FROM " + DatabaseHelper.TABLE_TECHNICIANS + " t " +
                "INNER JOIN " + DatabaseHelper.TABLE_SERVICES + " s ON t." + DatabaseHelper.KEY_ID + " = s." + DatabaseHelper.KEY_TECHNICIAN_ID +
                " WHERE s." + DatabaseHelper.KEY_CATEGORY_ID + " = ?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(categoryId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Technician technician = cursorToTechnician(cursor);
                technicians.add(technician);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return technicians;
    }

    private Technician cursorToTechnician(Cursor cursor) {
        return new Technician(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4));
    }
}
