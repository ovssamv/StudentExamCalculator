package com.example.GradesmManagment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.models.Module;
import com.example.adapters.ModuleAdapter;
import com.example.calculclau.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GradeCalculatorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ModuleAdapter adapter;
    private List<Module> moduleList;
    private Button calculateButton;
    private TextView resultText;
    private static final String JSON_URL = "https://num.univ-biskra.dz/psp/formations/get_modules_json?sem=1&spec=184";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_calculator);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Grade Calculator");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI elements
        resultText = findViewById(R.id.result_text);
        calculateButton = findViewById(R.id.calculate_button);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.modules_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize module list
        moduleList = new ArrayList<>();

        // Initialize adapter
        adapter = new ModuleAdapter(moduleList);
        recyclerView.setAdapter(adapter);

        // Set up calculate button
        calculateButton.setOnClickListener(v -> calculateWeightedAverage());

        // Load modules
        fetchModulesData();
    }

    private void showLoading(boolean show) {
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        RecyclerView recyclerView = findViewById(R.id.modules_recycler_view);

        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void fetchModulesData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        showLoading(true);

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

                        if (moduleList.isEmpty()) {
                            Toast.makeText(this, "No modules found in response", Toast.LENGTH_LONG).show();
                        }

                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("JSON_PARSE", "Error parsing JSON", e);
                        Toast.makeText(this, "Error parsing module data", Toast.LENGTH_LONG).show();
                    }
                    showLoading(false);
                },
                error -> {
                    Log.e("NETWORK", "Error fetching modules", error);
                    Toast.makeText(this, "Failed to load modules", Toast.LENGTH_LONG).show();

                    showLoading(false);
                }
        );

        // Set timeout and retry policy
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonArrayRequest);
    }



    private void calculateWeightedAverage() {
        Log.d("CALC_DEBUG", "Starting calculation");

        if (moduleList.isEmpty()) {
            resultText.setText("No modules loaded");
            Log.d("CALC_DEBUG", "Module list is empty");
            return;
        }

        double weightedSum = 0;
        double coefficientsSum = 0;
        int validModules = 0;

        for (Module module : moduleList) {
            // Debug current module state
            Log.d("MODULE_DEBUG", module.getName() +
                    " - TD:" + module.getTdScore() +
                    " TP:" + module.getTpScore() +
                    " Exam:" + module.getExamScore());

            // Skip if any score is invalid
            if (module.getTdScore() < 0 || module.getTdScore() > 20 ||
                    module.getTpScore() < 0 || module.getTpScore() > 20 ||
                    module.getExamScore() < 0 || module.getExamScore() > 20) {
                Log.d("CALC_DEBUG", "Skipping invalid scores for " + module.getName());
                continue;
            }

            module.calculateAverage();
            weightedSum += module.getAverage() * module.getCoefficient();
            coefficientsSum += module.getCoefficient();
            validModules++;
        }

        if (coefficientsSum == 0) {
            resultText.setText("Please enter valid grades (0-20)");
            Log.d("CALC_DEBUG", "No valid modules for calculation");
            return;
        }

        double weightedAverage = weightedSum / coefficientsSum;
        String resultMessage = String.format("Weighted Average: %.2f\n", weightedAverage);
        resultMessage += weightedAverage >= 10 ? "Result: Pass" : "Result: Fail";

        resultText.setText(resultMessage);
        Log.d("CALC_DEBUG", "Calculation complete: " + resultMessage);
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