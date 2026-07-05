package com.example.agrisense.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AgriVision.db";
    private static final int DATABASE_VERSION = 2;

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_ADDRESS = "address";

    // Crops table
    public static final String TABLE_CROPS = "crops";
    public static final String COL_CROP_ID = "id";
    public static final String COL_CROP_NAME = "cropName";
    public static final String COL_CROP_VARIETY = "variety";
    public static final String COL_CROP_PLANT_DATE = "plantDate";
    public static final String COL_CROP_HARVEST_DATE = "harvestDate";
    public static final String COL_CROP_AREA = "area";
    public static final String COL_CROP_NOTES = "notes";

    // Irrigation table
    public static final String TABLE_IRRIGATION = "irrigation";
    public static final String COL_IRR_ID = "id";
    public static final String COL_IRR_CROP_ID = "cropId";
    public static final String COL_IRR_DATE = "date";
    public static final String COL_IRR_STATUS = "status";

    // Fertilizer table
    public static final String TABLE_FERTILIZER = "fertilizer";
    public static final String COL_FERT_ID = "id";
    public static final String COL_FERT_CROP_ID = "cropId";
    public static final String COL_FERT_NAME = "fertilizer";
    public static final String COL_FERT_QUANTITY = "quantity";
    public static final String COL_FERT_DATE = "date";

    // Notes table
    public static final String TABLE_NOTES = "notes";
    public static final String COL_NOTE_ID = "id";
    public static final String COL_NOTE_TITLE = "title";
    public static final String COL_NOTE_DESC = "description";
    public static final String COL_NOTE_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_EMAIL + " TEXT, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_PHONE + " TEXT, " +
                COL_USER_ADDRESS + " TEXT)");

        // Create Crops Table
        db.execSQL("CREATE TABLE " + TABLE_CROPS + " (" +
                COL_CROP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CROP_NAME + " TEXT, " +
                COL_CROP_VARIETY + " TEXT, " +
                COL_CROP_PLANT_DATE + " TEXT, " +
                COL_CROP_HARVEST_DATE + " TEXT, " +
                COL_CROP_AREA + " TEXT, " +
                COL_CROP_NOTES + " TEXT)");

        // Create Irrigation Table
        db.execSQL("CREATE TABLE " + TABLE_IRRIGATION + " (" +
                COL_IRR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_IRR_CROP_ID + " INTEGER, " +
                COL_IRR_DATE + " TEXT, " +
                COL_IRR_STATUS + " TEXT)");

        // Create Fertilizer Table
        db.execSQL("CREATE TABLE " + TABLE_FERTILIZER + " (" +
                COL_FERT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FERT_CROP_ID + " INTEGER, " +
                COL_FERT_NAME + " TEXT, " +
                COL_FERT_QUANTITY + " TEXT, " +
                COL_FERT_DATE + " TEXT)");

        // Create Notes Table
        db.execSQL("CREATE TABLE " + TABLE_NOTES + " (" +
                COL_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOTE_TITLE + " TEXT, " +
                COL_NOTE_DESC + " TEXT, " +
                COL_NOTE_DATE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CROPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IRRIGATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FERTILIZER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // --- User Operations ---
    public boolean addUser(String name, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_PHONE, phone);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID}, 
                COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=? ", 
                new String[]{email, password}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // --- Crop Operations ---
    public boolean addCrop(String name, String variety, String plantDate, String harvestDate, String area, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CROP_NAME, name);
        values.put(COL_CROP_VARIETY, variety);
        values.put(COL_CROP_PLANT_DATE, plantDate);
        values.put(COL_CROP_HARVEST_DATE, harvestDate);
        values.put(COL_CROP_AREA, area);
        values.put(COL_CROP_NOTES, notes);
        long result = db.insert(TABLE_CROPS, null, values);
        return result != -1;
    }

    public Cursor getAllCrops() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CROPS, null);
    }

    public boolean deleteCrop(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CROPS, COL_CROP_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    // --- Irrigation Operations ---
    public boolean addIrrigation(int cropId, String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IRR_CROP_ID, cropId);
        values.put(COL_IRR_DATE, date);
        values.put(COL_IRR_STATUS, status);
        long result = db.insert(TABLE_IRRIGATION, null, values);
        return result != -1;
    }

    public boolean deleteIrrigation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_IRRIGATION, COL_IRR_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor getAllIrrigation() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT i." + COL_IRR_ID + ", i." + COL_IRR_DATE + ", i." + COL_IRR_STATUS + ", c." + COL_CROP_NAME + 
                " FROM " + TABLE_IRRIGATION + " i " +
                " JOIN " + TABLE_CROPS + " c ON i." + COL_IRR_CROP_ID + " = c." + COL_CROP_ID;
        return db.rawQuery(query, null);
    }

    // --- Fertilizer Operations ---
    public boolean addFertilizer(int cropId, String name, String quantity, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FERT_CROP_ID, cropId);
        values.put(COL_FERT_NAME, name);
        values.put(COL_FERT_QUANTITY, quantity);
        values.put(COL_FERT_DATE, date);
        long result = db.insert(TABLE_FERTILIZER, null, values);
        return result != -1;
    }

    public boolean deleteFertilizer(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FERTILIZER, COL_FERT_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor getAllFertilizers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT f." + COL_FERT_ID + ", f." + COL_FERT_NAME + ", f." + COL_FERT_QUANTITY + ", f." + COL_FERT_DATE + ", c." + COL_CROP_NAME +
                " FROM " + TABLE_FERTILIZER + " f " +
                " JOIN " + TABLE_CROPS + " c ON f." + COL_FERT_CROP_ID + " = c." + COL_CROP_ID;
        return db.rawQuery(query, null);
    }
    public int getCount(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // --- Notes Operations ---
    public boolean addNote(String title, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTE_TITLE, title);
        values.put(COL_NOTE_DESC, description);
        values.put(COL_NOTE_DATE, date);

        long result = db.insert(TABLE_NOTES, null, values);
        return result != -1;
    }

    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTES + " ORDER BY " + COL_NOTE_ID + " DESC", null);
    }

    public boolean deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NOTES, COL_NOTE_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }
}