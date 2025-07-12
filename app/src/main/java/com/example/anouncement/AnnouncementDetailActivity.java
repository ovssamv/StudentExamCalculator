package com.example.anouncement;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.DatabaseHelper;
import com.example.models.Announcement;
import com.example.calculclau.R;

public class AnnouncementDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView contentTextView;
    private TextView dateTextView;
    private TextView teacherNameTextView;
    private Button editButton;
    private Button deleteButton;

    private Announcement announcement;
    private DatabaseHelper dbHelper;
    private boolean isTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_detail);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Announcement Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI elements
        titleTextView = findViewById(R.id.announcement_title);
        contentTextView = findViewById(R.id.announcement_content);
        dateTextView = findViewById(R.id.announcement_date);
        teacherNameTextView = findViewById(R.id.announcement_teacher);
        editButton = findViewById(R.id.edit_announcement_button);
        deleteButton = findViewById(R.id.delete_announcement_button);

        // Get announcement ID from intent
        int announcementId = getIntent().getIntExtra("ANNOUNCEMENT_ID", -1);
        isTeacher = getIntent().getBooleanExtra("IS_TEACHER", false);

        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // Load announcement details
        if (announcementId != -1) {
            announcement = dbHelper.getAnnouncementById(announcementId);
            if (announcement != null) {
                displayAnnouncementDetails();
            } else {
                Toast.makeText(this, "Announcement not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Invalid announcement ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up edit and delete buttons (only visible to teachers)
        if (isTeacher) {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);

            editButton.setOnClickListener(v -> {
                // Open edit activity
                AddEditAnnouncementActivity.start(
                        AnnouncementDetailActivity.this,
                        announcement,
                        announcement.getTeacherId());
            });

            deleteButton.setOnClickListener(v -> {
                // Show confirmation dialog
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Delete Announcement")
                        .setMessage("Are you sure you want to delete this announcement?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (dbHelper.deleteAnnouncement(announcement.getId())) {
                                Toast.makeText(this, "Announcement deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to delete announcement", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        } else {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void displayAnnouncementDetails() {
        titleTextView.setText(announcement.getTitle());
        contentTextView.setText(announcement.getContent());
        dateTextView.setText("Posted on: " + announcement.getDate());
        teacherNameTextView.setText("By: " + announcement.getTeacherName());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}