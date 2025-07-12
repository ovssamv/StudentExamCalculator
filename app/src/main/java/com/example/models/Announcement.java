package com.example.models;

public class Announcement {
    private int id;
    private String title;
    private String content;
    private String date;
    private String teacherName;
    private String teacherId;

    // Default constructor
    public Announcement() {
    }

    // Constructor with all fields
    public Announcement(int id, String title, String content, String date, String teacherName, String teacherId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.teacherName = teacherName;
        this.teacherId = teacherId;
    }

    // Constructor without id (for new announcements)
    public Announcement(String title, String content, String date, String teacherName, String teacherId) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.teacherName = teacherName;
        this.teacherId = teacherId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}