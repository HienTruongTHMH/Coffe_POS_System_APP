package com.midterm.myposapplication.base;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.midterm.myposapplication.*;

public abstract class BaseActivity extends AppCompatActivity {
    
    protected UserManager userManager;
    protected OrderManager orderManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize common managers
        userManager = UserManager.getInstance(this);
        orderManager = OrderManager.getInstance();
    }
    
    /**
     * Setup bottom navigation - common for all activities
     */
    protected void setupBottomNavigation(int selectedItemId) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_bar);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(selectedItemId);
            bottomNav.setOnNavigationItemSelectedListener(this::handleBottomNavigation);
        }
    }
    
    /**
     * Handle bottom navigation item selection
     */
    private boolean handleBottomNavigation(android.view.MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_home) {
            navigateToActivity(MainActivity.class);
            return true;
        } else if (itemId == R.id.nav_tables) {
            navigateToActivity(TableSelectionActivity.class);
            return true;
        } else if (itemId == R.id.nav_orders || itemId == R.id.nav_cart) {
            navigateToActivity(Cart.class);
            return true;
        } else if (itemId == R.id.nav_profile) {
            navigateToActivity(Profile.class);
            return true;
        }
        
        return false;
    }
    
    /**
     * Navigate to another activity
     */
    protected void navigateToActivity(Class<?> activityClass) {
        if (!this.getClass().equals(activityClass)) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
            finish();
        }
    }
    
    /**
     * Show toast message
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show toast with string resource
     */
    protected void showToast(int stringResId) {
        Toast.makeText(this, getString(stringResId), Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Check if user is logged in
     */
    protected boolean isUserLoggedIn() {
        return userManager.isLoggedIn();
    }
    
    /**
     * Redirect to login if not authenticated
     */
    protected void checkAuthentication() {
        if (!isUserLoggedIn()) {
            Intent intent = new Intent(this, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}