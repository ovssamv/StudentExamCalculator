package com.example.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.DatabaseHelper;
import com.example.models.User;
import com.example.authentication.LoginActivity;
import com.example.calculclau.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameDisplay;
    private TextView emailDisplay;
    private TextView fullNameDisplay;
    private TextView dateOfBirthDisplay;
    private DatabaseHelper dbHelper;
    private static final String PREF_NAME = "LoginPrefs";
    public static final String KEY_USERNAME = "username";
    private static final String TAG = "ProfileActivity";
    private Button logoutButton;
    private Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // Get current username from SharedPreferences
        String currentUsername = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getString(KEY_USERNAME, "");

        Log.d(TAG, "Current username: " + currentUsername);

        // Initialize UI elements
        usernameDisplay = findViewById(R.id.username_display);
        emailDisplay = findViewById(R.id.email_display);
        fullNameDisplay = findViewById(R.id.full_name_display);
        dateOfBirthDisplay = findViewById(R.id.date_of_birth_display);
        editProfileButton = findViewById(R.id.edit_profile_button);

        // Load user details
        loadUserDetails(currentUsername);

        // Set up edit profile button
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to EditProfileActivity
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    // Check user type to determine which home activity to open
                    String userType = dbHelper.getUserType(currentUsername);
                    if ("teacher".equals(userType)) {
                        Intent intent = new Intent(ProfileActivity.this, TeacherActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    return true;

                } else if (id == R.id.nav_profile) {
                    // Already on profile screen
                    return true;
                }
                return false;
            }
        });

        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // Set the profile item as selected
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user details when returning to the activity
        String currentUsername = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getString(KEY_USERNAME, "");
        loadUserDetails(currentUsername);
    }

    private void loadUserDetails(String username) {
        // Get user details from database
        User user = dbHelper.getUserDetails(username);

        if (user != null) {
            // Set username
            if (usernameDisplay != null) {
                usernameDisplay.setText(user.getUsername());
                Log.d(TAG, "Username set in TextView");
            } else {
                Log.e(TAG, "usernameDisplay is null");
            }

            // Set email
            if (emailDisplay != null && user.getEmail() != null) {
                emailDisplay.setText(user.getEmail());
                Log.d(TAG, "Email set: " + user.getEmail());
            } else {
                Log.e(TAG, "Unable to set email: emailDisplay is null or email is null");
            }

            // Set full name
            if (fullNameDisplay != null && user.getFullName() != null) {
                fullNameDisplay.setText(user.getFullName());
            }

            // Set date of birth
            if (dateOfBirthDisplay != null && user.getDateOfBirth() != null) {
                dateOfBirthDisplay.setText(user.getDateOfBirth());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back when up button is pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Clear all user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to LoginActivity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close this activity
    }
}