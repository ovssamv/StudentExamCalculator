package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.DatabaseHelper;
import com.example.calculclau.R;

import java.util.List;
import java.util.Map;

public class GradeReportsAdapter extends RecyclerView.Adapter<GradeReportsAdapter.ReportViewHolder> {

    private final List<Map<String, String>> reportsList;
    private final DatabaseHelper dbHelper;

    public GradeReportsAdapter(List<Map<String, String>> reportsList, DatabaseHelper dbHelper) {
        this.reportsList = reportsList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Map<String, String> report = reportsList.get(position);

        holder.studentInfo.setText("Student: " + report.get("studentName") + " (" + report.get("studentId") + ")");
        holder.moduleInfo.setText("Module: " + report.get("moduleName") + " - " + report.get("type"));
        holder.issueInfo.setText("Issue: " + report.get("issue"));
        holder.dateInfo.setText("Date: " + report.get("date"));
        holder.statusInfo.setText("Status: " + report.get("status"));

        // Only show mark as reviewed button for pending reports
        if ("PENDING".equals(report.get("status"))) {
            holder.reviewButton.setVisibility(View.VISIBLE);
            holder.reviewButton.setOnClickListener(v -> {
                String reportId = report.get("id");
                if (dbHelper.updateReportStatus(reportId, "REVIEWED")) {
                    report.put("status", "REVIEWED");
                    holder.statusInfo.setText("Status: REVIEWED");
                    holder.reviewButton.setVisibility(View.GONE);
                    Toast.makeText(v.getContext(), "Report marked as reviewed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Failed to update report status", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.reviewButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reportsList.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView studentInfo;
        TextView moduleInfo;
        TextView issueInfo;
        TextView dateInfo;
        TextView statusInfo;
        Button reviewButton;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            studentInfo = itemView.findViewById(R.id.report_student_info);
            moduleInfo = itemView.findViewById(R.id.report_module_info);
            issueInfo = itemView.findViewById(R.id.report_issue_info);
            dateInfo = itemView.findViewById(R.id.report_date_info);
            statusInfo = itemView.findViewById(R.id.report_status_info);
            reviewButton = itemView.findViewById(R.id.mark_reviewed_button);
        }
    }
}
