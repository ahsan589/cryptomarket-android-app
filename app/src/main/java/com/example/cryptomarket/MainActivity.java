package com.example.cryptomarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import java.util.concurrent.TimeUnit;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.cryptomarket.databinding.ActivityMainBinding;
import com.example.cryptomarket.worker.PriceAlertWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavController navController;
    private WorkManager workManager;
    private SharedPreferences sharedPreferences;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private TextView emailTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        loginButton = findViewById(R.id.loginButton);
        Drawable logo = getResources().getDrawable(R.drawable.logi);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(logo, null, null, null);
        updateLoginButton();

        loginButton.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                // Redirect to Login Activity
                startActivity(new Intent(MainActivity.this, Login.class));
            } else {
                // Logout
                mAuth.signOut();
                updateLoginButton();
                updateDrawerHeader();
            }
        });
        workManager = WorkManager.getInstance(this);
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);

        // Set up DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        emailTextView = headerView.findViewById(R.id.email); // Reference the email TextView

        // Update header with email
        updateDrawerHeader();
        // Handle toggle switch for notifications
        View actionView = navigationView.getMenu().findItem(R.id.nav_notification_toggle).getActionView();
        Switch notificationSwitch = actionView.findViewById(R.id.notification_switch);

        boolean isEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        notificationSwitch.setChecked(isEnabled);

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notifications_enabled", isChecked);
            editor.apply();

            if (isChecked) {
                startPriceAlertWorker();
            } else {
                cancelPriceAlertWorker();
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_share) {
                navController.navigate(R.id.helpAndSupportFragment);
            } else if (id == R.id.about) {
                navController.navigate(R.id.aboutFragment);
            }
            else if (id == R.id.nav_socail) {
                navController.navigate(R.id.socialTradingFragment);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Set up BottomNavigationView with NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            BottomNavigationView bottomNavigationView = binding.bottomBar;
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                if (item.getItemId() != bottomNavigationView.getSelectedItemId()) {
                    return NavigationUI.onNavDestinationSelected(item, navController);
                }
                return true;
            });
        }

        // Start the worker if enabled
        if (isEnabled) {
            startPriceAlertWorker();
        }
    }

    private void updateDrawerHeader() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emailTextView.setText(user.getEmail());
        } else {
            emailTextView.setText("Not logged in");
        }
    }

    private void startPriceAlertWorker() {
        WorkRequest priceAlertWorkRequest = new PeriodicWorkRequest.Builder(
                PriceAlertWorker.class, 1, TimeUnit.MINUTES).build();
        workManager.enqueue(priceAlertWorkRequest);
    }

    private void cancelPriceAlertWorker() {
        workManager.cancelAllWorkByTag("PriceAlertWorker");
    }

    private void updateLoginButton() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            loginButton.setText("Login");
            loginButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.logi, 0, 0, 0);
        } else {
            loginButton.setText("Logout");
            loginButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lo, 0, 0, 0);
        }
    }
}

