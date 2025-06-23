package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class Profile extends AppCompatActivity {

    private TextView tvFullName, tvUserRole, tvUsername, tvEmail;
    private Button btnLogout;
    private MaterialToolbar toolbar;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ✅ SỬA LỖI: Đổi tên layout cho phù hợp
        setContentView(R.layout.account_profile);

        // Khởi tạo UserManager
        userManager = UserManager.getInstance(this);

        // Ánh xạ các view
        initializeViews();

        // Tải thông tin người dùng
        loadUserProfile();

        // Thiết lập sự kiện click
        setupClickListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvFullName = findViewById(R.id.tvFullName);
        tvUserRole = findViewById(R.id.tvUserRole);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserProfile() {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (userManager.isLoggedIn()) {
            User currentUser = userManager.getCurrentUser();
            if (currentUser != null) {
                // Hiển thị thông tin lên giao diện
                tvFullName.setText(currentUser.getFullName());
                tvUsername.setText(currentUser.getUsername());
                
                // Hiển thị và style cho chức vụ
                String role = currentUser.getRole().toString();
                tvUserRole.setText(role);
                
                // Đặt email mặc định dựa trên username
                tvEmail.setText(currentUser.getUsername() + "@coffee.com");
            }
        } else {
            // Nếu chưa đăng nhập, quay về màn hình đăng nhập
            redirectToLogin();
        }
    }

    private void setupClickListeners() {
        // Nút Back trên toolbar
        toolbar.setNavigationOnClickListener(v -> finish());

        // Nút Đăng xuất
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> logout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logout() {
        userManager.logoutUser();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, AuthActivity.class);
        // Xóa tất cả các activity trước đó khỏi stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}