package com.example.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DatabaseHelper;
import com.example.models.Module;
import com.example.calculclau.R;

import java.util.List;

public class StudentModuleAdapter extends RecyclerView.Adapter<StudentModuleAdapter.ModuleViewHolder> {

    private final List<Module> moduleList;

    public StudentModuleAdapter(List<Module> moduleList) {
        this.moduleList = moduleList;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_module_item, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    // Update ModuleViewHolder in StudentModuleAdapter.java to include the report button
    public static class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView moduleName;
        TextView moduleCoef;
        TextView tdScore;
        TextView tpScore;
        TextView examScore;
        TextView moduleAverage;
        Button reportButton;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleName = itemView.findViewById(R.id.module_name);
            moduleCoef = itemView.findViewById(R.id.module_coefficient);
            tdScore = itemView.findViewById(R.id.td_score);
            tpScore = itemView.findViewById(R.id.tp_score);
            examScore = itemView.findViewById(R.id.exam_score);
            moduleAverage = itemView.findViewById(R.id.module_average);
            reportButton = itemView.findViewById(R.id.report_grade_button);
        }
    }


    private String studentId;

    public StudentModuleAdapter(List<Module> moduleList, String studentId) {
        this.moduleList = moduleList;
        this.studentId = studentId;
    }


    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = moduleList.get(position);
        holder.moduleName.setText(module.getName());
        holder.moduleCoef.setText("Coefficient: " + module.getCoefficient());

        // Set the scores as text
        holder.tdScore.setText("TD: " + String.format("%.2f", module.getTdScore()));
        holder.tpScore.setText("TP: " + String.format("%.2f", module.getTpScore()));
        holder.examScore.setText("Exam: " + String.format("%.2f", module.getExamScore()));

        // Calculate and display module average
        module.calculateAverage();
        holder.moduleAverage.setText("Average: " + String.format("%.2f", module.getAverage()));

        //  report button click listener
        holder.reportButton.setOnClickListener(v -> {
            final String moduleName = module.getName();
            // dialog for reporting
            Context context = v.getContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Report Grade Issue");

            // Set up the input form
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_report_grade, null);
            builder.setView(dialogView);

            final RadioGroup typeGroup = dialogView.findViewById(R.id.report_type_group);
            final EditText issueText = dialogView.findViewById(R.id.report_issue_text);

            // Set up the buttons
            builder.setPositiveButton("Submit", (dialog, which) -> {
                // Get selected type
                int selectedId = typeGroup.getCheckedRadioButtonId();
                RadioButton radioButton = dialogView.findViewById(selectedId);
                String reportType = radioButton != null ? radioButton.getText().toString() : "UNKNOWN";
                String issue = issueText.getText().toString();

                // Save report
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
                long reportId = dbHelper.addGradeReport(studentId, moduleName, reportType, issue);

                if (reportId > 0) {
                    Toast.makeText(context, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to submit report", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });
    }
}