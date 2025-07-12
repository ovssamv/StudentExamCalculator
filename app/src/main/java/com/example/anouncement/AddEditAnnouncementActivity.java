package com.example.anouncement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.DatabaseHelper;
import com.example.models.Announcement;
import com.example.models.User;
import com.example.calculclau.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditAnnouncementActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private Button saveButton;
    private Button deleteButton;

    private String teacherId;
    private Announcement existingAnnouncement;
    private DatabaseHelper dbHelper;

    public static void start(Activity context, Announcement announcement, String teacherId) {
        Intent intent = new Intent(context, AddEditAnnouncementActivity.class);
        if (announcement != null) {
            intent.putExtra("ANNOUNCEMENT_ID", announcement.getId());
        }
        intent.putExtra("TEACHER_ID", teacherId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_announcement);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI elements
        titleEditText = findViewById(R.id.announcement_title_edit);
        contentEditText = findViewById(R.id.announcement_content_edit);
        saveButton = findViewById(R.id.save_announcement_button);
        deleteButton = findViewById(R.id.delete_announcement_button);

        // Get teacher ID from intent
        teacherId = getIntent().getStringExtra("TEACHER_ID");
        dbHelper = DatabaseHelper.getInstance(this);

        // Check if we're editing an existing announcement
        int announcementId = getIntent().getIntExtra("ANNOUNCEMENT_ID", -1);
        if (announcementId != -1) {
            // Load existing announcement
            existingAnnouncement = dbHelper.getAnnouncementById(announcementId);
            if (existingAnnouncement != null) {
                titleEditText.setText(existingAnnouncement.getTitle());
                contentEditText.setText(existingAnnouncement.getContent());
                getSupportActionBar().setTitle("Edit Announcement");

                // Only show delete button for existing announcements
                deleteButton.setVisibility(android.view.View.VISIBLE);
                deleteButton.setOnClickListener(v -> deleteAnnouncement());
            }
        } else {
            getSupportActionBar().setTitle("New Announcement");
            deleteButton.setVisibility(android.view.View.GONE);
        }

        // Set up save button
        saveButton.setOnClickListener(v -> saveAnnouncement());
    }

    private void saveAnnouncement() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty()) {
            titleEditText.setError("Title cannot be empty");
            return;
        }

        if (content.isEmpty()) {
            contentEditText.setError("Content cannot be empty");
            return;
        }

        // Get current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        // Get teacher name from database
        User teacher = dbHelper.getUserDetails(dbHelper.getUserNameByCustomId(teacherId));
        String teacherName = teacher != null ? teacher.getFullName() : "Unknown Teacher";

        if (existingAnnouncement != null) {
            // Update existing announcement
            existingAnnouncement.setTitle(title);
            existingAnnouncement.setContent(content);
            existingAnnouncement.setDate(currentDate);

            if (dbHelper.updateAnnouncement(existingAnnouncement)) {
                Toast.makeText(this, "Announcement updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update announcement", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Create new announcement
            Announcement announcement = new Announcement(
                    title, content, currentDate, teacherName, teacherId);

            if (dbHelper.addAnnouncement(announcement) != -1) {
                Toast.makeText(this, "Announcement added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add announcement", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteAnnouncement() {
        if (existingAnnouncement != null) {
            // Show confirmation dialog
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Announcement")
                    .setMessage("Are you sure you want to delete this announcement?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (dbHelper.deleteAnnouncement(existingAnnouncement.getId())) {
                            Toast.makeText(this, "Announcement deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to delete announcement", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
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