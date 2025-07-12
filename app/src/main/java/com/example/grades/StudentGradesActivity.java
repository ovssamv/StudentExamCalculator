package com.example.grades;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.DatabaseHelper;
import com.example.models.Module;
import com.example.adapters.ModuleAdapter;
import com.example.calculclau.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentGradesActivity extends AppCompatActivity {

    private TextView studentIdText;
    private TextView statusText;
    private ProgressBar progressBar;
    private RecyclerView modulesRecyclerView;
    private Button saveGradesButton;
    private DatabaseHelper dbHelper;
    private ModuleAdapter moduleAdapter;
    private List<Module> moduleList;
    private String studentId;
    private String studentName;
    private static final String JSON_URL = "https://num.univ-biskra.dz/psp/formations/get_modules_json?sem=1&spec=184";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_grades);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI components
        studentIdText = findViewById(R.id.student_id_text);
        statusText = findViewById(R.id.status_text);
        progressBar = findViewById(R.id.progress_bar);
        modulesRecyclerView = findViewById(R.id.modules_recycler_view);
        saveGradesButton = findViewById(R.id.save_grades_button);

        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // Initialize module list
        moduleList = new ArrayList<>();
        String currentStudentId = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                .getString("CUSTOM_ID", "");

        // Verify we're loading correct student
        Log.d("STUDENT_VIEW", "Viewing grades for student ID: " + currentStudentId);

        // Load grades with debug
        List<Module> grades = dbHelper.getModulesWithGrades(currentStudentId);
        Log.d("STUDENT_VIEW", "Loaded " + grades.size() + " modules");
        // Get student information from intent
        Intent intent = getIntent();
        if (intent != null) {
            studentId = intent.getStringExtra("STUDENT_ID");
            studentName = intent.getStringExtra("STUDENT_NAME");

            // Set title and student ID text
            getSupportActionBar().setTitle(studentName + "'s Grades");
            studentIdText.setText("Student ID: " + studentId);
        } else {
            // Handle error case
            Toast.makeText(this, "Error: Student information not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up RecyclerView
        modulesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter and set it to the RecyclerView
        moduleAdapter = new ModuleAdapter(moduleList);
        modulesRecyclerView.setAdapter(moduleAdapter);

        // Fetch modules data from JSON URL
        fetchModulesData();

        // Set up save button
        saveGradesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGrades();
            }
        });
    }

    private void fetchModulesData() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                JSON_URL,
                null,
                response -> {
                    try {
                        moduleList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject moduleObject = response.getJSONObject(i);
                            String name = moduleObject.getString("Nom_module");
                            double coefficient = moduleObject.getDouble("Coefficient");
                            Module module = new Module(name, coefficient);
                            moduleList.add(module);
                        }

                        // Load existing grades for this student
                        loadStudentGrades();

                        // Notify adapter of data change
                        moduleAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error parsing module data", Toast.LENGTH_LONG).show();
                        Log.e("JSON_PARSE", "Error parsing JSON", e);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load modules", Toast.LENGTH_LONG).show();
                    Log.e("NETWORK", "Error fetching modules", error);
                }
        );

        queue.add(jsonArrayRequest);
    }

    private void loadStudentGrades() {
        // Load grades from database for this student
        for (Module module : moduleList) {
            double[] scores = dbHelper.getStudentModuleGrades(studentId, module.getName());
            if (scores != null) {
                module.setTdScore(scores[0]);
                module.setTpScore(scores[1]);
                module.setExamScore(scores[2]);
            }
        }
    }

    private void saveGrades() {
        progressBar.setVisibility(View.VISIBLE);
        statusText.setText("Saving grades...");
        statusText.setVisibility(View.VISIBLE);

        boolean allSaved = true;
        int savedCount = 0;

        for (Module module : moduleList) {
            // Validate scores (0-20 range)
            if (module.getTdScore() < 0 || module.getTdScore() > 20 ||
                    module.getTpScore() < 0 || module.getTpScore() > 20 ||
                    module.getExamScore() < 0 || module.getExamScore() > 20) {
                Log.w("GradeValidation", "Invalid grades for module: " + module.getName());
                continue;
            }

            boolean saved = dbHelper.saveStudentModuleGrades(
                    studentId,
                    module.getName(),
                    module.getTdScore(),
                    module.getTpScore(),
                    module.getExamScore()
            );

            if (saved) {
                savedCount++;
            } else {
                allSaved = false;
                Log.e("GradeSave", "Failed to save grades for: " + module.getName());
            }
        }

        progressBar.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);

        if (savedCount > 0) {
            Toast.makeText(this, savedCount + " module grades saved", Toast.LENGTH_SHORT).show();

        }

        if (!allSaved) {
            Toast.makeText(this, "Some grades couldn't be saved", Toast.LENGTH_LONG).show();
        }

        // Reload grades to confirm they were saved
        loadStudentGrades();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}