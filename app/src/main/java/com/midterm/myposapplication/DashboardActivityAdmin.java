package com.midterm.myposapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class DashboardActivityAdmin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        // Initialize bottom navigation tabs
        LinearLayout listTab = findViewById(R.id.listTab);
        LinearLayout packageTab = findViewById(R.id.packageTab);
        LinearLayout cartTab = findViewById(R.id.cartTab);
        LinearLayout profileTab = findViewById(R.id.profileTab);

        // Set click listeners for bottom navigation
        listTab.setOnClickListener(v -> navigateToList());
        packageTab.setOnClickListener(v -> navigateToPackage());
        cartTab.setOnClickListener(v -> navigateToCart());
        profileTab.setOnClickListener(v -> navigateToProfile());
    }

    private void navigateToList() {
        // Implement navigation to List screen
    }

    private void navigateToPackage() {
        // Implement navigation to Package screen
    }

    private void navigateToCart() {
        // Implement navigation to Cart screen
    }

    private void navigateToProfile() {
        // Implement navigation to Profile screen
    }
}