package com.example.models;

public class User {
    private String username;
    private String email;
    private String password;
    private String userType;
    private String customId;
    private String fullName;
    private String dateOfBirth;

    // Getters and setters for all fields
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public String getId() { return customId; }
    public String getName() { return fullName; }
    public void setUserType(String userType) { this.userType = userType; }


    public void setCustomId(String customId) { this.customId = customId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}