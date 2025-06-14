package com.midterm.myposapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    // Xử lý thanh toán
    public void processPayment(View view) {
        // Giả sử bạn đã có subtotal, tax, và total_amount từ giỏ hàng
        double subtotal = 8.00;
        double tax = 0.80;
        double totalAmount = subtotal + tax;
        String tableNumber = "Table 1";
        String paymentMethod = "Banking";
        String paymentStatus = "Paid";

        ContentValues values = new ContentValues();
        values.put("table_number", tableNumber);
        values.put("subtotal", subtotal);
        values.put("tax", tax);
        values.put("total_amount", totalAmount);
        values.put("payment_method", paymentMethod);
        values.put("payment_status", paymentStatus);
        values.put("timestamp", System.currentTimeMillis());

        // Thêm hóa đơn vào cơ sở dữ liệu
        db.insert("Invoice", null, values);

        // Thông báo thành công
        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
    }
}
