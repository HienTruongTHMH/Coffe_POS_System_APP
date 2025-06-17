package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Create_invoice_cart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_invoice);

        // Initialize views
        ImageView backButton = findViewById(R.id.backButton);
        LinearLayout printInvoiceButton = findViewById(R.id.btninhoadon);
        LinearLayout createNewOrderButton = findViewById(R.id.btntaodonmoi);

        // Set click listener for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set click listener for print invoice button
        printInvoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printInvoice();
            }
        });

        // Set click listener for create new order button
        createNewOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewOrder();
            }
        });

        // Bottom navigation setup
        setupBottomNavigation();

        // Populate with sample data (replace with your actual data)
        Invoice sampleInvoice = new Invoice(
                "Cà phê gói",
                "15/06/2023",
                "INV-2023-001",
                "520.000 VND",
                "https://example.com/qr-code.png"
        );
        populateInvoiceData(sampleInvoice);
    }

    private void printInvoice() {
        // Implement your printing logic here
        Toast.makeText(this, "Đang in hóa đơn...", Toast.LENGTH_SHORT).show();
    }

    private void createNewOrder() {
        Intent intent = new Intent(this, Order.class);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        LinearLayout orderTab = findViewById(R.id.orderTab);
        LinearLayout listTab = findViewById(R.id.listTab);
        LinearLayout packageTab = findViewById(R.id.packageTab);
        LinearLayout cartTab = findViewById(R.id.cartTab);
        LinearLayout profileTab = findViewById(R.id.profileTab);

        orderTab.setOnClickListener(v -> navigateToTab(Order.class));
        listTab.setOnClickListener(v -> navigateToTab(TableAdapter.class));
        packageTab.setOnClickListener(v -> navigateToTab(Package.class));
        cartTab.setOnClickListener(v -> navigateToTab(Cart.class));
        profileTab.setOnClickListener(v -> navigateToTab(Profile.class));
    }

    private void navigateToTab(Class<?> destinationClass) {
        Intent intent = new Intent(this, destinationClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void populateInvoiceData(Invoice invoice) {
        TextView productName = findViewById(R.id.productName);
        TextView productDescription = findViewById(R.id.productDescription);
        TextView deliveryDate = findViewById(R.id.deliveryDate);
        TextView orderCode = findViewById(R.id.orderCode);
        TextView totalAmount = findViewById(R.id.totalAmount);
        ImageView qrCode = findViewById(R.id.qrCode);

        productName.setText(invoice.getProductName());
        productDescription.setText(invoice.getProductDescription());
        deliveryDate.setText(invoice.getDeliveryDate());
        orderCode.setText(invoice.getOrderCode());
        totalAmount.setText(invoice.getTotalAmount());

        // Load QR code image using Glide or Picasso
        // Glide.with(this).load(invoice.getQrCodeUrl()).into(qrCode);
    }
}

// Invoice model class
class Invoice {
    private String productName;
    private String productDescription;
    private String deliveryDate;
    private String orderCode;
    private String totalAmount;
    private String qrCodeUrl;

    public Invoice(String productName, String deliveryDate, String orderCode,
                   String totalAmount, String qrCodeUrl) {
        this.productName = productName;
        this.deliveryDate = deliveryDate;
        this.orderCode = orderCode;
        this.totalAmount = totalAmount;
        this.qrCodeUrl = qrCodeUrl;
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }
}