package com.example.fixit_v2.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.fixit_v2.database.DatabaseHelper;
import com.example.fixit_v2.models.User;

public class UserDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { 
        DatabaseHelper.KEY_ID, 
        DatabaseHelper.KEY_USERNAME, 
        DatabaseHelper.KEY_PASSWORD, 
        DatabaseHelper.KEY_EMAIL, // Added
        DatabaseHelper.KEY_ROLE 
    };

    public UserDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public User createUser(String username, String password, String email, String role) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USERNAME, username);
        values.put(DatabaseHelper.KEY_PASSWORD, password);
        values.put(DatabaseHelper.KEY_EMAIL, email); // Added
        values.put(DatabaseHelper.KEY_ROLE, role);
        
        long insertId = database.insert(DatabaseHelper.TABLE_USERS, null, values);
        if (insertId == -1) return null; 

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, allColumns, DatabaseHelper.KEY_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        User newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    public User login(String username, String password) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                allColumns,
                DatabaseHelper.KEY_USERNAME + " = ? AND " + DatabaseHelper.KEY_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public User getUserById(int userId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, allColumns,
                DatabaseHelper.KEY_ID + " = ?", 
                new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        if(cursor != null) cursor.close();
        return null;
    }

    private User cursorToUser(Cursor cursor) {
        // ID, USERNAME, PASSWORD, EMAIL, ROLE
        return new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
    }
}
