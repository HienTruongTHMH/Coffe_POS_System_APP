package com.midterm.myposapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast; // Để hiển thị thông báo

import androidx.appcompat.app.AppCompatActivity;

public class EditTextDetailAdmin extends AppCompatActivity {

    private EditText editTextTax;
    private EditText editTextPricingPolicy;
    private EditText editTextNotes;
    private Button paymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_invoice_details); // Thay your_layout_file bằng tên file XML của bạn

        // 1. Ánh xạ các EditText và Button từ layout bằng ID
        editTextTax = findViewById(R.id.editTextTax);
        editTextPricingPolicy = findViewById(R.id.editTextPricingPolicy);
        editTextNotes = findViewById(R.id.editTextNotes);
        paymentButton = findViewById(R.id.paymentButton); // Giả sử nút THANH TOÁN có ID là paymentButton

        // 2. Thiết lập Listener cho nút "THANH TOÁN"
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3. Lấy dữ liệu từ các EditText
                String taxInfo = editTextTax.getText().toString();
                String pricingPolicyInfo = editTextPricingPolicy.getText().toString();
                String notesInfo = editTextNotes.getText().toString();

                // 4. Xử lý dữ liệu (ví dụ: hiển thị Toast hoặc gửi đi)
                String message = "Thuế: " + taxInfo + "\n" +
                        "Chính sách giá: " + pricingPolicyInfo + "\n" +
                        "Ghi chú: " + notesInfo;
                Toast.makeText(EditTextDetailAdmin.this, message, Toast.LENGTH_LONG).show();

                // Ở đây bạn sẽ thực hiện logic để lưu dữ liệu vào database
                // hoặc gửi lên server, v.v.
            }
        });

        // Bạn cũng có thể thiết lập listener cho nút Back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Quay lại màn hình trước đó
            }
        });
    }
}

