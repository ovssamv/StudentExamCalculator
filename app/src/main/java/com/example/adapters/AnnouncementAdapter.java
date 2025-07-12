package com.example.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anouncement.AnnouncementDetailActivity;
import com.example.models.Announcement;
import com.example.calculclau.R;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    private Context context;
    private List<Announcement> announcementList;
    private boolean isTeacher; // To determine if delete option should be shown

    public AnnouncementAdapter(Context context, List<Announcement> announcementList, boolean isTeacher) {
        this.context = context;
        this.announcementList = announcementList;
        this.isTeacher = isTeacher;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);

        holder.titleTextView.setText(announcement.getTitle());
        holder.dateTextView.setText(announcement.getDate());
        holder.teacherNameTextView.setText("By: " + announcement.getTeacherName());

        // Set up click listener to view announcement details
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AnnouncementDetailActivity.class);
            intent.putExtra("ANNOUNCEMENT_ID", announcement.getId());
            intent.putExtra("IS_TEACHER", isTeacher);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    // Update the announcements list
    public void updateAnnouncements(List<Announcement> announcements) {
        this.announcementList = announcements;
        notifyDataSetChanged();
    }

    static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleTextView;
        TextView dateTextView;
        TextView teacherNameTextView;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.announcement_card);
            titleTextView = itemView.findViewById(R.id.announcement_title);
            dateTextView = itemView.findViewById(R.id.announcement_date);
            teacherNameTextView = itemView.findViewById(R.id.announcement_teacher);
        }
    }
}