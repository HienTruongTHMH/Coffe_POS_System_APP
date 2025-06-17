package com.midterm.myposapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etVerifyPassword;
    private Button btnConfirmRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Liên kết layout với Activity

        // Liên kết các phần tử từ layout XML
        etUsername = findViewById(R.id.etRegUsername);
        etPassword = findViewById(R.id.etRegPassword);
        etVerifyPassword = findViewById(R.id.etVerifyPassword);
        btnConfirmRegister = findViewById(R.id.btnConfirmRegister);

        // Tạo đối tượng DatabaseHelper để quản lý cơ sở dữ liệu
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Xử lý sự kiện khi người dùng nhấn nút đăng ký
        btnConfirmRegister.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String verifyPassword = etVerifyPassword.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (username.isEmpty() || password.isEmpty() || verifyPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(verifyPassword)) {
                Toast.makeText(RegisterActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem tên người dùng đã tồn tại chưa
            if (dbHelper.isUsernameExists(username)) {
                Toast.makeText(RegisterActivity.this, "Tên người dùng đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thêm người dùng vào cơ sở dữ liệu
            long newRowId = dbHelper.addUser(username, password);

            // Kiểm tra việc lưu thành công hay không
            if (newRowId != -1) {
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                finish(); // Quay lại LoginActivity sau khi đăng ký thành công
            } else {
                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
