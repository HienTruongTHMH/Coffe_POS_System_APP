package com.midterm.myposapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Profile extends AppCompatActivity {

    // Constants
    private static final String PROFILE_PREFS = "profile_prefs";
    private static final String USER_PREFS = "user_prefs";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_REVENUE = "revenue";
    private static final String KEY_AVATAR = "avatar";
    private static final int PICK_IMAGE_REQUEST = 100;

    // UI Components
    private ImageView btnBack, imgAvatar, btnEditAvatar;
    private TextView tvName, tvPhone, tvEmail;
    private TextView tvEmployeeName, tvEmployeePhone, tvEmployeeEmail, tvShopAddress, tvMonthlyRevenue;
    private ImageButton btnEditPhone, btnEditEmail;
    private Button btnLogout, btnChangeAccount;
    private LinearLayout navOrder, navList, navPackage, navCart, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_profile);

        initViews();
        setupListeners();
        loadProfileData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnEditAvatar = findViewById(R.id.btnEditAvatar);
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvEmployeeName = findViewById(R.id.tvEmployeeName);
        tvEmployeePhone = findViewById(R.id.tvEmployeePhone);
        tvEmployeeEmail = findViewById(R.id.tvEmployeeEmail);
        tvShopAddress = findViewById(R.id.tvShopAddress);
        tvMonthlyRevenue = findViewById(R.id.tvMonthlyRevenue);
        btnEditPhone = findViewById(R.id.btnEditPhone);
        btnEditEmail = findViewById(R.id.btnEditEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnChangeAccount = findViewById(R.id.btnChangeAccount);

        navOrder = findViewById(R.id.navOrder);
        navList = findViewById(R.id.navList);
        navPackage = findViewById(R.id.navPackage);
        navCart = findViewById(R.id.navCart);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupListeners() {
        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Chỉnh sửa avatar
        imgAvatar.setOnClickListener(v -> openImagePicker());
        btnEditAvatar.setOnClickListener(v -> openImagePicker());

        // Sửa số điện thoại
        btnEditPhone.setOnClickListener(v -> showEditDialog(
                "Số điện thoại",
                tvEmployeePhone.getText().toString(),
                InputType.TYPE_CLASS_PHONE,
                newValue -> updatePhoneNumber(newValue)
        ));

        // Sửa email
        btnEditEmail.setOnClickListener(v -> showEditDialog(
                "Email",
                tvEmployeeEmail.getText().toString(),
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                newValue -> updateEmail(newValue)
        ));
        // Đăng xuất
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // Đổi tài khoản
        btnChangeAccount.setOnClickListener(v -> switchAccount());

        // Thanh điều hướng
        navOrder.setOnClickListener(v -> goToActivity(Order.class));
        navList.setOnClickListener(v -> goToActivity(TableAdapter.class));
        navPackage.setOnClickListener(v -> goToActivity(Package.class));

    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences(PROFILE_PREFS, MODE_PRIVATE);

        // Đặt giá trị mặc định nếu không tìm thấy
        String defaultName = "Phan Nhất Việt";
        String defaultPhone = "0795625822";
        String defaultEmail = "Nhatviet29@gmail.com";
        String defaultAddress = "12 Lê Duẩn";
        String defaultRevenue = "10.000.000";

        tvName.setText(prefs.getString(KEY_NAME, defaultName));
        tvPhone.setText(prefs.getString(KEY_PHONE, defaultPhone));
        tvEmail.setText(prefs.getString(KEY_EMAIL, defaultEmail));
        tvEmployeeName.setText(prefs.getString(KEY_NAME, defaultName));
        tvEmployeePhone.setText(prefs.getString(KEY_PHONE, defaultPhone));
        tvEmployeeEmail.setText(prefs.getString(KEY_EMAIL, defaultEmail));
        tvShopAddress.setText(prefs.getString(KEY_ADDRESS, defaultAddress));
        tvMonthlyRevenue.setText(prefs.getString(KEY_REVENUE, defaultRevenue));

        // Load avatar
        String encodedImage = prefs.getString(KEY_AVATAR, null);
        if (encodedImage != null) {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgAvatar.setImageBitmap(decodedBitmap);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Bitmap circularBitmap = getCircularBitmap(bitmap);
                imgAvatar.setImageBitmap(circularBitmap);

                // Lưu avatar vào SharedPreferences
                saveAvatar(bitmap);
                showToast("Đã cập nhật ảnh đại diện");
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Không thể tải ảnh");
            }
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int min = Math.min(width, height);
        Bitmap output = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);

        android.graphics.Canvas canvas = new android.graphics.Canvas(output);
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAntiAlias(true);

        canvas.drawCircle(min / 2f, min / 2f, min / 2f, paint);
        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));

        android.graphics.Rect rect = new android.graphics.Rect(0, 0, width, height);
        android.graphics.RectF rectF = new android.graphics.RectF(0, 0, min, min);
        canvas.drawBitmap(bitmap, rect, rectF, paint);

        return output;
    }

    private void saveAvatar(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        SharedPreferences.Editor editor = getSharedPreferences(PROFILE_PREFS, MODE_PRIVATE).edit();
        editor.putString(KEY_AVATAR, encodedImage);
        editor.apply();
    }

    // Các phương thức khác giữ nguyên...
    private void updatePhoneNumber(String newPhone) {
        if (isValidPhone(newPhone)) {
            tvEmployeePhone.setText(newPhone);
            tvPhone.setText(newPhone);
            saveProfileData(KEY_PHONE, newPhone);
        } else {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmail(String newEmail) {
        if (isValidEmail(newEmail)) {
            tvEmployeeEmail.setText(newEmail);
            tvEmail.setText(newEmail);
            saveProfileData(KEY_EMAIL, newEmail);
        } else {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{10,11}$");
    }

    private boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void saveProfileData(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(PROFILE_PREFS, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
        showToast("Cập nhật thành công");
    }

    private void showEditDialog(String title, String currentValue, int inputType, OnSaveListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa " + title);

        final EditText input = new EditText(this);
        input.setInputType(inputType);
        input.setText(currentValue);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            if (!newValue.isEmpty()) {
                listener.onSave(newValue);
            } else {
                showToast("Vui lòng nhập thông tin");
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
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
        // ✅ SỬA LỖI: Sử dụng UserManager để đăng xuất và chuyển đến AuthActivity
        UserManager.getInstance(this).logoutUser();
        
        Intent intent = new Intent(this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void switchAccount() {
        // ✅ SỬA LỖI: Chức năng chuyển tài khoản cũng nên thực hiện đăng xuất
        logout();
    }

    private void goToActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    interface OnSaveListener {
        void onSave(String newValue);
    }
}