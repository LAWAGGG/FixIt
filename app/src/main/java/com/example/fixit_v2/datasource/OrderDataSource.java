package com.example.fixit_v2.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.fixit_v2.database.DatabaseHelper;
import com.example.fixit_v2.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {
            DatabaseHelper.KEY_ID,
            DatabaseHelper.KEY_USER_ID,
            DatabaseHelper.KEY_SERVICE_ID,
            DatabaseHelper.KEY_ADDRESS,
            DatabaseHelper.KEY_ORDER_DATE,
            DatabaseHelper.KEY_STATUS,
            DatabaseHelper.KEY_NOTES,
            DatabaseHelper.KEY_COMPLETION_IMAGE,
            DatabaseHelper.KEY_PAYMENT_METHOD,
            DatabaseHelper.KEY_PAYMENT_STATUS
    };

    public OrderDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (database != null) {
            database.close();
        }
    }

    public void createOrder(int userId, int serviceId, String address, String orderDate, String status, String notes, String paymentMethod, String paymentStatus) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USER_ID, userId);
        values.put(DatabaseHelper.KEY_SERVICE_ID, serviceId);
        values.put(DatabaseHelper.KEY_ADDRESS, address);
        values.put(DatabaseHelper.KEY_ORDER_DATE, orderDate);
        values.put(DatabaseHelper.KEY_STATUS, status);
        values.put(DatabaseHelper.KEY_NOTES, notes);
        values.put(DatabaseHelper.KEY_PAYMENT_METHOD, paymentMethod);
        values.put(DatabaseHelper.KEY_PAYMENT_STATUS, paymentStatus);
        database.insert(DatabaseHelper.TABLE_ORDERS, null, values);
    }

    // --- NEW METHOD TO UPDATE STATUS ---
    public int updateOrderStatus(int orderId, String newStatus) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_STATUS, newStatus);
        return database.update(DatabaseHelper.TABLE_ORDERS, values, DatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(orderId)});
    }

    // --- NEW METHOD FOR COMPLETION ---
    public int updateOrderWithCompletion(int orderId, String status, String completionImage) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_STATUS, status);
        values.put(DatabaseHelper.KEY_COMPLETION_IMAGE, completionImage);
        return database.update(DatabaseHelper.TABLE_ORDERS, values, DatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(orderId)});
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_ORDERS, allColumns, DatabaseHelper.KEY_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            orders.add(cursorToOrder(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return orders;
    }

    public List<Order> getOrdersByTechnicianId(int technicianId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.* FROM " + DatabaseHelper.TABLE_ORDERS + " o " +
                "INNER JOIN " + DatabaseHelper.TABLE_SERVICES + " s ON o." + DatabaseHelper.KEY_SERVICE_ID + " = s." + DatabaseHelper.KEY_ID +
                " WHERE s." + DatabaseHelper.KEY_TECHNICIAN_ID + " = ?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(technicianId)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            orders.add(cursorToOrder(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return orders;
    }

    public Order getOrderById(int orderId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_ORDERS, allColumns, DatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(orderId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Order order = cursorToOrder(cursor);
            cursor.close();
            return order;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public int updatePaymentStatus(int orderId, String paymentStatus) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_PAYMENT_STATUS, paymentStatus);
        return database.update(DatabaseHelper.TABLE_ORDERS, values, DatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(orderId)});
    }

    private Order cursorToOrder(Cursor cursor) {
        return new Order(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getString(9)
        );
    }
}
