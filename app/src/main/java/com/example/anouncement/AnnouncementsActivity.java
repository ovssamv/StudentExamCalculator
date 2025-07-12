package com.example.anouncement;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DatabaseHelper;
import com.example.adapters.AnnouncementAdapter;
import com.example.models.Announcement;
import com.example.calculclau.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AnnouncementsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AnnouncementAdapter adapter;
    private TextView emptyView;
    private FloatingActionButton addAnnouncementFab;
    private boolean isTeacher;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Announcements");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI elements
        recyclerView = findViewById(R.id.announcements_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        addAnnouncementFab = findViewById(R.id.add_announcement_fab);

        // Get user type and ID from intent
        isTeacher = getIntent().getBooleanExtra("IS_TEACHER", false);
        userId = getIntent().getStringExtra("USER_ID");

        // Show FAB only for teachers
        if (isTeacher) {
            addAnnouncementFab.setVisibility(View.VISIBLE);
            addAnnouncementFab.setOnClickListener(v -> {
                // Open activity to add a new announcement
                AddEditAnnouncementActivity.start(this, null, userId);
            });
        } else {
            addAnnouncementFab.setVisibility(View.GONE);
        }

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadAnnouncements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload announcements when returning to this activity
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        // Get announcements from database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        List<Announcement> announcements = dbHelper.getAllAnnouncements();

        if (announcements.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            // Set up adapter
            if (adapter == null) {
                adapter = new AnnouncementAdapter(this, announcements, isTeacher);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateAnnouncements(announcements);
            }
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