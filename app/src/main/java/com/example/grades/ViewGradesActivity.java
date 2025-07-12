package com.example.grades;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.adapters.StudentModuleAdapter;
import com.example.calculclau.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewGradesActivity extends AppCompatActivity {

    private String studentId;
    private RecyclerView recyclerView;
    private StudentModuleAdapter adapter;
    private List<Module> moduleList;
    private TextView resultText;
    private DatabaseHelper dbHelper;
    private static final String JSON_URL = "https://num.univ-biskra.dz/psp/formations/get_modules_json?sem=1&spec=184";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_grades);
        moduleList = new ArrayList<>();
        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Grades");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get student information from Intent
        Intent intent = getIntent();
        if (intent != null) {
            studentId = intent.getStringExtra("STUDENT_ID");
        }

        // Initialize UI elements
        resultText = findViewById(R.id.result_text);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.modules_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize module list
        moduleList = new ArrayList<>();

        // Initialize adapter with read-only mode for students
        adapter = new StudentModuleAdapter(moduleList, studentId);
        recyclerView.setAdapter(adapter);


        // Load modules and grades
        fetchModulesData();
    }

    private void fetchModulesData() {
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
                            moduleList.add(new Module(name, coefficient));
                        }

                        loadStudentGrades();
                        adapter.notifyDataSetChanged();
                        calculateWeightedAverage();

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing module data", Toast.LENGTH_LONG).show();
                        Log.e("JSON_PARSE", "Error parsing JSON", e);
                    }
                },
                error -> {
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
                // Calculate module average
                module.calculateAverage();
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void calculateWeightedAverage() {
        if (moduleList.isEmpty()) {
            resultText.setText("No modules loaded");
            return;
        }

        double weightedSum = 0;
        double coefficientsSum = 0;

        for (Module module : moduleList) {
            module.calculateAverage();
            weightedSum += module.getAverage() * module.getCoefficient();
            coefficientsSum += module.getCoefficient();
        }

        double weightedAverage = weightedSum / coefficientsSum;
        String resultMessage = "Weighted Average: " + String.format("%.2f", weightedAverage) + "\n";

        if (weightedAverage >= 10) {
            resultMessage += "Result: Pass";
        } else {
            resultMessage += "Result: Fail";
        }

        resultText.setText(resultMessage);
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