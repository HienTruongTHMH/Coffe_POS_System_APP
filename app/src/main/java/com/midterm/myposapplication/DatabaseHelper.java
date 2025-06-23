package com.midterm.myposapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "PosApp.db";
    // ✅ SỬA LỖI: Tăng phiên bản DB để kích hoạt onUpgrade, tạo lại bảng users
    private static final int DATABASE_VERSION = 2; 
    
    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_IS_ACTIVE = "is_active";
    
    // Create users table SQL
    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " TEXT PRIMARY KEY,"
            + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL,"
            + COLUMN_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_ROLE + " TEXT NOT NULL,"
            + COLUMN_FULL_NAME + " TEXT,"
            + COLUMN_CREATED_AT + " INTEGER,"
            + COLUMN_IS_ACTIVE + " INTEGER DEFAULT 1"
            + ")";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        insertDefaultUsers(db);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu tồn tại và tạo lại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // Bạn cũng có thể thêm DROP TABLE cho các bảng khác ở đây nếu cần
        onCreate(db);
        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
    }
    
    private void insertDefaultUsers(SQLiteDatabase db) {
        // Insert default admin user
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USER_ID, "admin_001");
        adminValues.put(COLUMN_USERNAME, "admin");
        adminValues.put(COLUMN_PASSWORD, "admin123");
        adminValues.put(COLUMN_ROLE, UserRole.ADMIN.getValue());
        adminValues.put(COLUMN_FULL_NAME, "Quản lý");
        adminValues.put(COLUMN_CREATED_AT, System.currentTimeMillis());
        adminValues.put(COLUMN_IS_ACTIVE, 1);
        db.insert(TABLE_USERS, null, adminValues);
        
        // Insert default employee user
        ContentValues employeeValues = new ContentValues();
        employeeValues.put(COLUMN_USER_ID, "emp_001");
        employeeValues.put(COLUMN_USERNAME, "employee");
        employeeValues.put(COLUMN_PASSWORD, "emp123");
        employeeValues.put(COLUMN_ROLE, UserRole.EMPLOYEE.getValue());
        employeeValues.put(COLUMN_FULL_NAME, "Nhân viên");
        employeeValues.put(COLUMN_CREATED_AT, System.currentTimeMillis());
        employeeValues.put(COLUMN_IS_ACTIVE, 1);
        db.insert(TABLE_USERS, null, employeeValues);
        
        Log.d(TAG, "Default users inserted");
    }
    
    public User authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ? AND " + COLUMN_IS_ACTIVE + " = 1",
                new String[]{username, password},
                null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setRole(UserRole.fromString(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)));
            user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
            user.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVE)) == 1);
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return user;
    }
    
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USERNAME},
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null);
        
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        
        return exists;
    }
    
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_USER_ID, user.getUserId());
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_ROLE, user.getRole().getValue());
        values.put(COLUMN_FULL_NAME, user.getFullName());
        values.put(COLUMN_CREATED_AT, user.getCreatedAt());
        values.put(COLUMN_IS_ACTIVE, user.isActive() ? 1 : 0);
        
        return db.insert(TABLE_USERS, null, values);
    }
}
