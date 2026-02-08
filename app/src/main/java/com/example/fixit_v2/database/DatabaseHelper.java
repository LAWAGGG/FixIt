package com.example.fixit_v2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FixIt.db";
    private static final int DATABASE_VERSION = 19; // Final Seeder Version

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
            // ========== USERS ========== 
            // Admin: 1, Techs: 2-21, Users: 22-41
            String sqlUser = "INSERT INTO " + TABLE_USERS + " (username, password, email, role) VALUES (?, ?, ?, ?);";
            SQLiteStatement stmtUser = db.compileStatement(sqlUser);
            stmtUser.bindString(1, "admin"); stmtUser.bindString(2, "admin123"); stmtUser.bindString(3, "admin@fixit.com"); stmtUser.bindString(4, "Admin"); stmtUser.executeInsert();
            for (int i = 1; i <= 20; i++) {
                stmtUser.clearBindings(); stmtUser.bindString(1, "tech" + i); stmtUser.bindString(2, "tech123"); stmtUser.bindString(3, "tech"+i+"@fixit.com"); stmtUser.bindString(4, "Technician"); stmtUser.executeInsert();
                stmtUser.clearBindings(); stmtUser.bindString(1, "user" + i); stmtUser.bindString(2, "user123"); stmtUser.bindString(3, "user"+i+"@example.com"); stmtUser.bindString(4, "User"); stmtUser.executeInsert();
            }
            stmtUser.close();

            // ========== TECHNICIANS ========== 
            String sqlTech = "INSERT INTO " + TABLE_TECHNICIANS + " (user_id, name, phone_number, earnings) VALUES (?, ?, ?, ?);";
            SQLiteStatement stmtTech = db.compileStatement(sqlTech);
            String[] techNames = {"Budi Santoso", "Joko Susilo", "Siti Aminah", "Agus Wijaya", "Dewi Lestari"};
            for (int i = 0; i < techNames.length; i++) {
                stmtTech.clearBindings(); stmtTech.bindLong(1, 2 + i); stmtTech.bindString(2, techNames[i]); stmtTech.bindString(3, "0812345678" + i); stmtTech.bindDouble(4, 0); stmtTech.executeInsert();
            }
            stmtTech.close();

            // ========== SERVICE CATEGORIES ========== 
            String sqlCat = "INSERT INTO " + TABLE_SERVICE_CATEGORIES + " (category_name) VALUES (?);";
            SQLiteStatement stmtCat = db.compileStatement(sqlCat);
            String[] categories = {"Servis AC", "Instalasi Listrik", "Perbaikan Pipa"};
            for (String category : categories) { stmtCat.clearBindings(); stmtCat.bindString(1, category); stmtCat.executeInsert(); }
            stmtCat.close();

            // ========== SERVICES ========== 
            String sqlService = "INSERT INTO " + TABLE_SERVICES + " (service_name, description, price, technician_id, category_id) VALUES (?, ?, ?, ?, ?);";
            SQLiteStatement stmtService = db.compileStatement(sqlService);
            Object[][] servicesData = {{"Cuci AC 1/2 PK", "Pembersihan unit indoor & outdoor.", 75000.0, 1, 1}, {"Bongkar Pasang AC", "Jasa pemindahan unit AC.", 250000.0, 2, 1}, {"Perbaikan Stop Kontak", "Memperbaiki stop kontak rusak.", 50000.0, 3, 2}};
            for (Object[] service : servicesData) { stmtService.clearBindings(); stmtService.bindString(1, (String) service[0]); stmtService.bindString(2, (String) service[1]); stmtService.bindDouble(3, (Double) service[2]); stmtService.bindLong(4, (Integer) service[3]); stmtService.bindLong(5, (Integer) service[4]); stmtService.executeInsert(); }
            stmtService.close();
            
            // ========== ORDERS (Corrected Logic) ========== 
            String sqlOrder = "INSERT INTO " + TABLE_ORDERS + " (user_id, service_id, address, order_date, status) VALUES (?, ?, ?, ?, ?);";
            SQLiteStatement stmtOrder = db.compileStatement(sqlOrder);
            // user1 (ID 22) orders service 1 (from tech 1)
            stmtOrder.bindLong(1, 22); stmtOrder.bindLong(2, 1); stmtOrder.bindString(3, "Jl. Merdeka No. 1, Jakarta"); stmtOrder.bindString(4, "2024-05-10"); stmtOrder.bindString(5, "Selesai"); stmtOrder.executeInsert();
            // user2 (ID 23) orders service 3 (from tech 3)
            stmtOrder.bindLong(1, 23); stmtOrder.bindLong(2, 3); stmtOrder.bindString(3, "Jl. Sudirman No. 12, Jakarta"); stmtOrder.bindString(4, "2024-05-11"); stmtOrder.bindString(5, "Selesai"); stmtOrder.executeInsert();
            // user1 (ID 22) also orders service 2 (from tech 2)
            stmtOrder.bindLong(1, 22); stmtOrder.bindLong(2, 2); stmtOrder.bindString(3, "Jl. Thamrin No. 15, Jakarta"); stmtOrder.bindString(4, "2024-05-12"); stmtOrder.bindString(5, "Menunggu"); stmtOrder.executeInsert();
            stmtOrder.close();

            // ========== REVIEWS (Corrected Logic) ========== 
            String sqlReview = "INSERT INTO " + TABLE_REVIEWS + " (service_id, user_id, rating, comment) VALUES (?, ?, ?, ?);";
            SQLiteStatement stmtReview = db.compileStatement(sqlReview);
            // Review for service 1 from user 22
            stmtReview.bindLong(1, 1); stmtReview.bindLong(2, 22); stmtReview.bindLong(3, 5); stmtReview.bindString(4, "Sangat bersih dan profesional!"); stmtReview.executeInsert();
            // Review for service 3 from user 23
            stmtReview.bindLong(1, 3); stmtReview.bindLong(2, 23); stmtReview.bindLong(3, 4); stmtReview.bindString(4, "Lumayan bagus, teknisi datang tepat waktu."); stmtReview.executeInsert();
            stmtReview.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
