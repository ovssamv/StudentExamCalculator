package com.example.GradesmManagment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.DatabaseHelper;
import com.example.calculclau.R;
import com.example.grades.StudentGradesActivity;

public class StudentsListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView studentsListView;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Students List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // Initialize list view
        studentsListView = findViewById(R.id.students_list_view);
        studentsListView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) adapter.getItem(position);
            @SuppressLint("Range") String studentId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_STUDENT_ID));
            @SuppressLint("Range") String studentName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_STUDENT_NAME));

            Intent intent = new Intent(StudentsListActivity.this, StudentGradesActivity.class);
            intent.putExtra("STUDENT_ID", studentId);
            intent.putExtra("STUDENT_NAME", studentName);
            startActivity(intent);
        });
        // Load students data
        loadStudentsList();
    }

    private void loadStudentsList() {
        try {
            // Get cursor with student data
            Cursor cursor = dbHelper.getAllStudents();

            // Define which columns to display
            String[] fromColumns = {
                    DatabaseHelper.KEY_STUDENT_ID,
                    DatabaseHelper.KEY_STUDENT_NAME,
                    DatabaseHelper.KEY_STUDENT_EMAIL
            };

            // Define view IDs to map the data to
            int[] toViews = {
                    R.id.student_id_text,
                    R.id.student_name_text,
                    R.id.student_email_text
            };

            // Create adapter
            adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.student_list_item,
                    cursor,
                    fromColumns,
                    toViews,
                    0
            );

            // Set adapter to list view
            studentsListView.setAdapter(adapter);

            // Show message if no students
            if (cursor.getCount() == 0) {
                Toast.makeText(this, "No students found in the database", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading students: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}