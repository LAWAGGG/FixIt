package com.example.fixit_v2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FixIt.db";
    private static final int DATABASE_VERSION = 20; // Final Seeder Version

    public static final String TABLE_USERS = "users";
    public static final String TABLE_TECHNICIANS = "technicians";
    public static final String TABLE_SERVICE_CATEGORIES = "service_categories";
    public static final String TABLE_SERVICES = "services";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_PAYMENTS = "payments";
    public static final String TABLE_REVIEWS = "reviews";
    public static final String KEY_ID = "id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_ROLE = "role";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_EARNINGS = "earnings";
    public static final String KEY_CATEGORY_NAME = "category_name";
    public static final String KEY_SERVICE_NAME = "service_name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PRICE = "price";
    public static final String KEY_TECHNICIAN_ID = "technician_id";
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_SERVICE_ID = "service_id";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_ORDER_DATE = "order_date";
    public static final String KEY_STATUS = "status";
    public static final String KEY_ORDER_ID = "order_id";
    public static final String KEY_METHOD = "method";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_RATING = "rating";
    public static final String KEY_COMMENT = "comment";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_USERNAME + " TEXT UNIQUE NOT NULL, " + KEY_PASSWORD + " TEXT NOT NULL, " + KEY_EMAIL + " TEXT NOT NULL, " + KEY_PHONE + " TEXT, " + KEY_ROLE + " TEXT NOT NULL);";
    private static final String CREATE_TABLE_TECHNICIANS = "CREATE TABLE " + TABLE_TECHNICIANS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_USER_ID + " INTEGER, " + KEY_NAME + " TEXT NOT NULL, " + KEY_PHONE_NUMBER + " TEXT, " + KEY_EARNINGS + " REAL DEFAULT 0, FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "));";
    private static final String CREATE_TABLE_SERVICE_CATEGORIES = "CREATE TABLE " + TABLE_SERVICE_CATEGORIES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CATEGORY_NAME + " TEXT NOT NULL);";
    private static final String CREATE_TABLE_SERVICES = "CREATE TABLE " + TABLE_SERVICES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_SERVICE_NAME + " TEXT NOT NULL, " + KEY_DESCRIPTION + " TEXT, " + KEY_PRICE + " REAL NOT NULL, " + KEY_TECHNICIAN_ID + " INTEGER, " + KEY_CATEGORY_ID + " INTEGER, FOREIGN KEY(" + KEY_TECHNICIAN_ID + ") REFERENCES " + TABLE_TECHNICIANS + "(" + KEY_ID + "), FOREIGN KEY(" + KEY_CATEGORY_ID + ") REFERENCES " + TABLE_SERVICE_CATEGORIES + "(" + KEY_ID + "));";
    private static final String CREATE_TABLE_ORDERS = "CREATE TABLE " + TABLE_ORDERS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_USER_ID + " INTEGER, " + KEY_SERVICE_ID + " INTEGER, " + KEY_ADDRESS + " TEXT NOT NULL, " + KEY_ORDER_DATE + " TEXT NOT NULL, " + KEY_STATUS + " TEXT NOT NULL, FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "), FOREIGN KEY(" + KEY_SERVICE_ID + ") REFERENCES " + TABLE_SERVICES + "(" + KEY_ID + "));";
    private static final String CREATE_TABLE_PAYMENTS = "CREATE TABLE " + TABLE_PAYMENTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_ORDER_ID + " INTEGER, " + KEY_METHOD + " TEXT NOT NULL, " + KEY_AMOUNT + " REAL NOT NULL, FOREIGN KEY(" + KEY_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + KEY_ID + "));";
    private static final String CREATE_TABLE_REVIEWS = "CREATE TABLE " + TABLE_REVIEWS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_SERVICE_ID + " INTEGER, " + KEY_USER_ID + " INTEGER, " + KEY_RATING + " INTEGER, " + KEY_COMMENT + " TEXT, FOREIGN KEY(" + KEY_SERVICE_ID + ") REFERENCES " + TABLE_SERVICES + "(" + KEY_ID + "), FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "));";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_TECHNICIANS);
        db.execSQL(CREATE_TABLE_SERVICE_CATEGORIES);
        db.execSQL(CREATE_TABLE_SERVICES);
        db.execSQL(CREATE_TABLE_ORDERS);
        db.execSQL(CREATE_TABLE_PAYMENTS);
        db.execSQL(CREATE_TABLE_REVIEWS);
        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TECHNICIANS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void seedData(SQLiteDatabase db) {
        db.beginTransaction();
        try {

            // ================= USERS =================
            String sqlUser = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
            SQLiteStatement userStmt = db.compileStatement(sqlUser);

            // Admin
            userStmt.bindString(1, "admin");
            userStmt.bindString(2, "admin123");
            userStmt.bindString(3, "admin@fixit.com");
            userStmt.bindString(4, "Admin");
            userStmt.executeInsert();

            // 20 Technicians
            for (int i = 1; i <= 20; i++) {
                userStmt.clearBindings();
                userStmt.bindString(1, "tech" + i);
                userStmt.bindString(2, "tech123");
                userStmt.bindString(3, "tech" + i + "@fixit.com");
                userStmt.bindString(4, "Technician");
                userStmt.executeInsert();
            }

            // 30 Users
            for (int i = 1; i <= 30; i++) {
                userStmt.clearBindings();
                userStmt.bindString(1, "user" + i);
                userStmt.bindString(2, "user123");
                userStmt.bindString(3, "user" + i + "@mail.com");
                userStmt.bindString(4, "User");
                userStmt.executeInsert();
            }
            userStmt.close();

            // ================= TECHNICIANS =================
            String sqlTech = "INSERT INTO technicians (user_id, name, phone_number, earnings) VALUES (?, ?, ?, ?)";
            SQLiteStatement techStmt = db.compileStatement(sqlTech);

            for (int i = 1; i <= 20; i++) {
                techStmt.clearBindings();
                techStmt.bindLong(1, i + 1); // admin = 1
                techStmt.bindString(2, "Technician " + i);
                techStmt.bindString(3, "08123" + (100000 + i));
                techStmt.bindDouble(4, 0);
                techStmt.executeInsert();
            }
            techStmt.close();

            // ================= CATEGORIES =================
            String[] categories = {
                    "Servis AC", "Instalasi Listrik", "Perbaikan Pipa",
                    "Service Mesin Cuci", "Service Kulkas"
            };

            SQLiteStatement catStmt = db.compileStatement(
                    "INSERT INTO service_categories (category_name) VALUES (?)"
            );

            for (String cat : categories) {
                catStmt.clearBindings();
                catStmt.bindString(1, cat);
                catStmt.executeInsert();
            }
            catStmt.close();

            // ================= SERVICES (25 DATA) =================
            SQLiteStatement serviceStmt = db.compileStatement(
                    "INSERT INTO services (service_name, description, price, technician_id, category_id) VALUES (?, ?, ?, ?, ?)"
            );

            for (int i = 1; i <= 25; i++) {
                serviceStmt.clearBindings();
                serviceStmt.bindString(1, "Service #" + i);
                serviceStmt.bindString(2, "Deskripsi layanan " + i);
                serviceStmt.bindDouble(3, 50000 + (i * 10000));
                serviceStmt.bindLong(4, (i % 20) + 1);   // technician
                serviceStmt.bindLong(5, (i % 5) + 1);    // category
                serviceStmt.executeInsert();
            }
            serviceStmt.close();

            // ================= ORDERS (30 DATA) =================
            SQLiteStatement orderStmt = db.compileStatement(
                    "INSERT INTO orders (user_id, service_id, address, order_date, status) VALUES (?, ?, ?, ?, ?)"
            );

            for (int i = 1; i <= 30; i++) {
                orderStmt.clearBindings();
                orderStmt.bindLong(1, 22 + (i % 30)); // user
                orderStmt.bindLong(2, (i % 25) + 1);  // service
                orderStmt.bindString(3, "Alamat pelanggan ke-" + i);
                orderStmt.bindString(4, "2024-05-" + ((i % 28) + 1));
                orderStmt.bindString(5, i % 2 == 0 ? "Selesai" : "Menunggu");
                orderStmt.executeInsert();
            }
            orderStmt.close();

            // ================= PAYMENTS =================
            SQLiteStatement payStmt = db.compileStatement(
                    "INSERT INTO payments (order_id, method, amount) VALUES (?, ?, ?)"
            );

            for (int i = 1; i <= 20; i++) {
                payStmt.clearBindings();
                payStmt.bindLong(1, i);
                payStmt.bindString(2, "Cash");
                payStmt.bindDouble(3, 100000 + (i * 5000));
                payStmt.executeInsert();
            }
            payStmt.close();

            // ================= REVIEWS =================
            SQLiteStatement reviewStmt = db.compileStatement(
                    "INSERT INTO reviews (service_id, user_id, rating, comment) VALUES (?, ?, ?, ?)"
            );

            for (int i = 1; i <= 20; i++) {
                reviewStmt.clearBindings();
                reviewStmt.bindLong(1, (i % 25) + 1);
                reviewStmt.bindLong(2, 22 + i);
                reviewStmt.bindLong(3, (i % 5) + 1);
                reviewStmt.bindString(4, "Review pelanggan ke-" + i);
                reviewStmt.executeInsert();
            }
            reviewStmt.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
