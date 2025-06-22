package com.midterm.myposapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Cấu hình cơ sở dữ liệu
    // Database version and name

    // Table name
    private static final String TABLE_ORDERS = "orders";

    // Orders table columns

    private static final String COLUMN_TABLE_NUMBER = "table_number";
    private static final String COLUMN_PAYMENT_METHOD = "payment_method";
    private static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_CREATED_AT = "created_at";
    public static final String DATABASE_NAME = "CoffeeShopDB";  // Đổi tên thành CoffeeShopDB
    public static final String TABLE_USERS = "users";           // Bảng người dùng
    public static final String COLUMN_ID = "id";                // Cột ID
    public static final String COLUMN_USERNAME = "username";    // Cột tên người dùng
    public static final String COLUMN_PASSWORD = "password";    // Cột mật khẩu

    private static final int DATABASE_VERSION = 1;

    // Câu lệnh tạo bảng người dùng
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT);";

    // Câu lệnh tạo bảng cho các món
    private static final String CREATE_TABLE_DRINKS = "CREATE TABLE Drinks (" +
            "drink_id TEXT PRIMARY KEY, " +
            "drink_name TEXT NOT NULL, " +
            "price DECIMAL(5, 2) NOT NULL, " +
            "imageResId INTEGER NOT NULL);";

    // Câu lệnh tạo bảng cho giỏ hàng
    private static final String CREATE_TABLE_CART = "CREATE TABLE Cart (" +
            "cart_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "drink_id TEXT NOT NULL, " +
            "quantity INTEGER NOT NULL, " +
            "size TEXT CHECK(size IN ('S', 'M', 'L')), " +
            "total_price DECIMAL(5, 2) NOT NULL, " +
            "FOREIGN KEY (drink_id) REFERENCES Drinks(drink_id));";

    // Câu lệnh tạo bảng cho hóa đơn
    private static final String CREATE_TABLE_INVOICE = "CREATE TABLE Invoice (" +
            "invoice_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "table_number TEXT NOT NULL, " +
            "subtotal DECIMAL(5, 2) NOT NULL, " +
            "tax DECIMAL(5, 2) NOT NULL, " +
            "total_amount DECIMAL(5, 2) NOT NULL, " +
            "payment_method TEXT CHECK(payment_method IN ('Banking', 'Cashing')) NOT NULL, " +
            "payment_status TEXT CHECK(payment_status IN ('Pending', 'Paid')) NOT NULL, " +
            "timestamp LONG NOT NULL, " +
            "FOREIGN KEY (invoice_id) REFERENCES Cart(cart_id));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);  // Tạo bảng người dùng
        db.execSQL(CREATE_TABLE_DRINKS);  // Tạo bảng món uống
        db.execSQL(CREATE_TABLE_CART);  // Tạo bảng giỏ hàng
        db.execSQL(CREATE_TABLE_INVOICE);  // Tạo bảng hóa đơn
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);  // Xóa bảng người dùng nếu có
        db.execSQL("DROP TABLE IF EXISTS Drinks");  // Xóa bảng món uống nếu có
        db.execSQL("DROP TABLE IF EXISTS Cart");  // Xóa bảng giỏ hàng nếu có
        db.execSQL("DROP TABLE IF EXISTS Invoice");  // Xóa bảng hóa đơn nếu có
        onCreate(db);  // Tạo lại các bảng mới
    }

    // Phương thức thêm người dùng mới
    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        // Chèn dữ liệu vào bảng và trả về ID của hàng vừa chèn
        return db.insert(TABLE_USERS, null, values);
    }

    // Phương thức kiểm tra đăng nhập (kiểm tra tài khoản và mật khẩu)
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD},
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;  // Nếu tìm thấy người dùng với tài khoản và mật khẩu đúng
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return false;  // Không tìm thấy người dùng
        }
    }

    // Phương thức kiểm tra xem tên người dùng đã tồn tại chưa
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;  // Tên người dùng đã tồn tại
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return false;  // Tên người dùng chưa tồn tại
        }
    }

    public long addOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TABLE_NUMBER, order.getTableNumber());
//        values.put(COLUMN_PAYMENT_METHOD, order.getPaymentMethod());
        values.put(COLUMN_TOTAL_AMOUNT, order.getTotalAmount());
        values.put(COLUMN_STATUS, "completed"); // Default status

        // Insert row
        long id = db.insert(TABLE_ORDERS, null, values);

        // Close database connection
        db.close();

        return id;
    }

    @Override
    public void close() {
        super.close();
    }
}
