package com.midterm.myposapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignUp; // TextView cho "Sign Up" link

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Liên kết layout với Activity

        // Liên kết các phần tử từ layout XML
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp); // Liên kết với TextView "Sign Up"

        // Tạo đối tượng DatabaseHelper để truy cập cơ sở dữ liệu
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Xử lý sự kiện khi người dùng nhấn nút đăng nhập
        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Kiểm tra nếu người dùng không nhập tài khoản hoặc mật khẩu
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
                return; // Dừng lại nếu các trường trống
            }

            // Mở cơ sở dữ liệu để đọc
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Truy vấn cơ sở dữ liệu để kiểm tra tài khoản người dùng
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_PASSWORD},
                    DatabaseHelper.COLUMN_USERNAME + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?",
                    new String[]{username, password},
                    null, null, null
            );

            // Kiểm tra xem có kết quả trả về không (tức là tài khoản và mật khẩu hợp lệ)
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();  // Di chuyển đến kết quả đầu tiên (nếu có)
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                // Chuyển sang màn hình chính (hoặc màn hình mong muốn)
                startActivity(new Intent(this, MainActivity.class));
                finish(); // Đảm bảo không quay lại màn hình đăng nhập khi bấm nút back
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }

            // Đóng con trỏ khi xong
            if (cursor != null) {
                cursor.close();
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào "Sign Up"
        tvSignUp.setOnClickListener(v -> {
            // Chuyển sang RegisterActivity (màn hình đăng ký)
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}
