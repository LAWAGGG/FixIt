package com.example.fixit_v2.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.fixit_v2.database.DatabaseHelper;
import com.example.fixit_v2.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { DatabaseHelper.KEY_ID, DatabaseHelper.KEY_SERVICE_ID, DatabaseHelper.KEY_USER_ID, DatabaseHelper.KEY_RATING, DatabaseHelper.KEY_COMMENT };

    public ReviewDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Review createReview(int serviceId, int userId, int rating, String comment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_SERVICE_ID, serviceId);
        values.put(DatabaseHelper.KEY_USER_ID, userId);
        values.put(DatabaseHelper.KEY_RATING, rating);
        values.put(DatabaseHelper.KEY_COMMENT, comment);
        long insertId = database.insert(DatabaseHelper.TABLE_REVIEWS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_REVIEWS, allColumns, DatabaseHelper.KEY_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Review newReview = cursorToReview(cursor);
        cursor.close();
        return newReview;
    }

    public List<Review> getReviewsByServiceId(int serviceId) {
        List<Review> reviews = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_REVIEWS, allColumns,
                DatabaseHelper.KEY_SERVICE_ID + " = ?",
                new String[]{String.valueOf(serviceId)}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            reviews.add(cursorToReview(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return reviews;
    }

    public float getAverageRating(int serviceId) {
        Cursor cursor = database.rawQuery("SELECT AVG(" + DatabaseHelper.KEY_RATING + ") FROM " + DatabaseHelper.TABLE_REVIEWS + " WHERE " + DatabaseHelper.KEY_SERVICE_ID + " = " + serviceId, null);
        if (cursor != null && cursor.moveToFirst()) {
            float avg = cursor.getFloat(0);
            cursor.close();
            return avg;
        }
        if (cursor != null) cursor.close();
        return 0;
    }

    private Review cursorToReview(Cursor cursor) {
        return new Review(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4));
    }
}
