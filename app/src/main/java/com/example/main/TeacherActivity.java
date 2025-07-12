package com.example.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;


import com.example.DatabaseHelper;
import com.example.GradesmManagment.StudentsListActivity;
import com.example.adapters.GradeReportsAdapter;
import com.example.anouncement.AnnouncementsActivity;
import com.example.authentication.LoginActivity;
import com.example.calculclau.R;
import com.example.calculclau.databinding.ActivityTeacherBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TeacherActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private TextView teacherId;
    private String userType;
    private String customId;
    private ActivityTeacherBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_teacher);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Teacher Dashboard");

        // Initialize UI elements
        welcomeMessage = findViewById(R.id.welcome_message);
        teacherId = findViewById(R.id.teacher_id);

        // Get user information from Intent
        Intent intent = getIntent();
        if (intent != null) {
            userType = intent.getStringExtra("USER_TYPE");
            customId = intent.getStringExtra("CUSTOM_ID");

            // Set welcome message
            welcomeMessage.setText("Welcome, Teacher!");
            teacherId.setText("Teacher ID: " + customId);
        }

        // Set up Students button
        Button viewStudentsButton = findViewById(R.id.view_students_button);
        viewStudentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start StudentsListActivity
                Intent intent = new Intent(TeacherActivity.this, StudentsListActivity.class);
                startActivity(intent);
            }
        });

        // Set up Modules button
        Button viewModulesButton = findViewById(R.id.view_modules_button);
        viewModulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start ModuleViewActivity
                Intent intent = new Intent(TeacherActivity.this, ModuleViewActivity.class);
                startActivity(intent);
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    // Already on home screen
                    return true;
                } else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(TeacherActivity.this, ProfileActivity.class);
                    intent.putExtra("USER_TYPE", userType);
                    intent.putExtra("CUSTOM_ID", customId);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        Button viewAnnouncementsButton = findViewById(R.id.view_announcements_button);
        viewAnnouncementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start AnnouncementsActivity
                Intent intent = new Intent(TeacherActivity.this, AnnouncementsActivity.class);
                intent.putExtra("IS_TEACHER", true);
                intent.putExtra("USER_ID", customId); // customId is already available from your existing code
                startActivity(intent);
            }
        });
        Button viewReportsButton = findViewById(R.id.view_grade_reports_button);
        viewReportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGradeReports();
            }
        });

        // Set the home item as selected
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            // Navigate to Profile Activity
            Intent intent = new Intent(TeacherActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.action_logout) {
            // Clear any saved login info in SharedPreferences
            getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            // Navigate back to login screen
            Intent intent = new Intent(TeacherActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void showGradeReports() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Grade Reports");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_grade_reports, null);
        builder.setView(dialogView);

        RecyclerView reportsRecyclerView = dialogView.findViewById(R.id.reports_recycler_view);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get reports from database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        List<Map<String, String>> reports = dbHelper.getGradeReports();

        if (reports.isEmpty()) {
            TextView emptyTextView = dialogView.findViewById(R.id.empty_reports_text);
            emptyTextView.setVisibility(View.VISIBLE);
            reportsRecyclerView.setVisibility(View.GONE);
        } else {
            // Create adapter and set it
            GradeReportsAdapter adapter = new GradeReportsAdapter(reports, dbHelper);
            reportsRecyclerView.setAdapter(adapter);
        }

        builder.setPositiveButton("Close", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user information when returning to this activity
        if (customId == null) {
            // Try to get user information from SharedPreferences if not available
            String savedUserType = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                    .getString("USER_TYPE", null);
            String savedCustomId = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                    .getString("CUSTOM_ID", null);
            
            if (savedUserType != null && savedCustomId != null) {
                userType = savedUserType;
                customId = savedCustomId;
                welcomeMessage.setText("Welcome, Teacher!");
                teacherId.setText("Teacher ID: " + customId);
            }
        }
    }
}
