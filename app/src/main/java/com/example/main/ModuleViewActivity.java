package com.example.main;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.example.models.Module;
import com.example.adapters.ModuleViewAdapter; // Changed to use the new adapter
import com.example.calculclau.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ModuleViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ModuleViewAdapter adapter; // Changed to use ModuleViewAdapter
    private List<Module> moduleList;
    private static final String JSON_URL = "https://num.univ-biskra.dz/psp/formations/get_modules_json?sem=1&spec=184";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_view);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Modules");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.modules_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize module list
        moduleList = new ArrayList<>();

        // Initialize adapter with the new ModuleViewAdapter
        adapter = new ModuleViewAdapter(moduleList);
        recyclerView.setAdapter(adapter);

        // Load modules data
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
                            Module module = new Module(name, coefficient);
                            moduleList.add(module);
                        }

                        adapter.notifyDataSetChanged();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}