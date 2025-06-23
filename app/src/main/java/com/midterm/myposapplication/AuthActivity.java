package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private DatabaseHelper dbHelper;
    private UserManager userManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        
        // Initialize components
        initializeComponents();
        
        // Check if user is already logged in
        if (userManager.isLoggedIn()) {
            redirectToAppropriateActivity();
            return;
        }
        
        setupClickListeners();
    }
    
    private void initializeComponents() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        
        dbHelper = new DatabaseHelper(this);
        userManager = UserManager.getInstance(this);
    }
    
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        tvSignUp.setOnClickListener(v -> showRegisterDialog());
    }
    
    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        
        User user = dbHelper.authenticateUser(username, password);
        
        if (user != null) {
            userManager.loginUser(user);
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            redirectToAppropriateActivity();
        } else {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void redirectToAppropriateActivity() {
        Intent intent;
        
        if (userManager.isAdmin()) {
            intent = new Intent(this, DashboardActivityAdmin.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
    
    private void showRegisterDialog() {
        Toast.makeText(this, "Liên hệ quản trị viên để tạo tài khoản mới", Toast.LENGTH_LONG).show();
    }
}