package com.example.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.GradesmManagment.GradeCalculatorActivity;
import com.example.anouncement.AnnouncementsActivity;
import com.example.grades.ViewGradesActivity;
import com.example.authentication.LoginActivity;
import com.example.calculclau.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private TextView userId;
    private String userType;
    private String customId;
    private Button viewGradesButton;
    private Button calculatorButton;
    private Button viewAnnouncementsButton;
    private static final String PREF_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Student Dashboard");

        // Initialize views
        welcomeMessage = findViewById(R.id.welcome_message);
        userId = findViewById(R.id.user_id);
        viewGradesButton = findViewById(R.id.view_grades_button);
        calculatorButton = findViewById(R.id.calculator_button);
        viewAnnouncementsButton = findViewById(R.id.view_announcements_button);

        // Get user information from Intent
        Intent intent = getIntent();
        if (intent != null) {
            userType = intent.getStringExtra("USER_TYPE");
            customId = intent.getStringExtra("CUSTOM_ID");

            // Set welcome message
            welcomeMessage.setText("Welcome, Student!");
            userId.setText("Student ID: " + customId);
        }

        // View Grades button
        viewGradesButton.setOnClickListener(v -> {
            // Add logging
            Log.d("MainActivity", "View grades button clicked, customId=" + customId);

            // Use the customId that was already retrieved from the intent
            if (customId != null && !customId.isEmpty()) {
                Intent sintent = new Intent(this, ViewGradesActivity.class);
                sintent.putExtra("STUDENT_ID", customId);
                try {
                    startActivity(sintent);
                    Log.d("MainActivity", "Started ViewGradesActivity successfully");
                } catch (Exception e) {
                    Log.e("MainActivity", "Error starting ViewGradesActivity", e);
                    Toast.makeText(this, "Error opening grades: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("MainActivity", "Student ID not found or empty");
                Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        // Calculate Grades button
        calculatorButton.setOnClickListener(v -> {
            Log.d("MainActivity", "Calculate grades button clicked");
            Intent calcIntent = new Intent(MainActivity.this, GradeCalculatorActivity.class);
            try {
                startActivity(calcIntent);
                Log.d("MainActivity", "Started GradeCalculatorActivity successfully");
            } catch (Exception e) {
                Log.e("MainActivity", "Error starting GradeCalculatorActivity", e);
                Toast.makeText(this, "Error opening calculator: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // View Announcements button
        viewAnnouncementsButton.setOnClickListener(v -> {
            Log.d("MainActivity", "View announcements button clicked");
            if (customId != null) {
                Intent intent1 = new Intent(MainActivity.this, AnnouncementsActivity.class);
                intent1.putExtra("IS_TEACHER", false); // false for student
                intent1.putExtra("USER_ID", customId);
                try {
                    startActivity(intent1);
                    Log.d("MainActivity", "Started AnnouncementsActivity successfully");
                } catch (Exception e) {
                    Log.e("MainActivity", "Error starting AnnouncementsActivity", e);
                    Toast.makeText(this, "Error opening announcements: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    // Already on home screen
                    return true;
                } else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("USER_TYPE", userType);
                    intent.putExtra("CUSTOM_ID", customId);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Set the home item as selected
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            // Navigate to Profile Activity
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_grades) {
            // Navigate to View Grades Activity
            Intent intent = new Intent(MainActivity.this, ViewGradesActivity.class);
            intent.putExtra("STUDENT_ID", customId);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            // Clear any saved login info in SharedPreferences
            getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            // Navigate back to login screen
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user information when returning to this activity
        if (customId == null) {
            // Try to get user information from SharedPreferences if not available
            String savedUserType = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    .getString("USER_TYPE", null);
            String savedCustomId = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    .getString("CUSTOM_ID", null);
            
            if (savedUserType != null && savedCustomId != null) {
                userType = savedUserType;
                customId = savedCustomId;
                welcomeMessage.setText("Welcome, Student!");
                userId.setText("Student ID: " + customId);
            }
        }
    }
}