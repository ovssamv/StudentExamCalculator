package com.example.main;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.DatabaseHelper;
import com.example.models.User;
import com.example.calculclau.R;

import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText emailInput;
    private EditText fullNameInput;
    private EditText dateOfBirthInput;
    private Button saveChangesButton;
    private DatabaseHelper dbHelper;
    private static final String PREF_NAME = "LoginPrefs";
    public static final String KEY_USERNAME = "username";
    private String originalUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");
        }

        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // Initialize UI elements
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        fullNameInput = findViewById(R.id.full_name_input);
        dateOfBirthInput = findViewById(R.id.date_of_birth_input);
        saveChangesButton = findViewById(R.id.save_changes_button);

        // Get current username from SharedPreferences
        originalUsername = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getString(KEY_USERNAME, "");

        // Load current user information
        loadUserInfo(originalUsername);

        // Set up date picker for date of birth
        dateOfBirthInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set up save button click listener
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSaveChanges();
            }
        });
    }

    private void loadUserInfo(String username) {
        // Get user details from database
        User user = dbHelper.getUserDetails(username);

        if (user != null) {
            usernameInput.setText(user.getUsername());
            emailInput.setText(user.getEmail());
            fullNameInput.setText(user.getFullName());
            dateOfBirthInput.setText(user.getDateOfBirth());
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        // Parse existing date if available
        String currentDate = dateOfBirthInput.getText().toString();
        if (!TextUtils.isEmpty(currentDate) && currentDate.matches("\\d{2}/\\d{2}/\\d{4}")) {
            String[] parts = currentDate.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1; // Month is 0-indexed
            int year = Integer.parseInt(parts[2]);
            calendar.set(year, month, day);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Update the EditText with selected date
                        String dateString = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                        dateOfBirthInput.setText(dateString);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void attemptSaveChanges() {
        // Reset errors
        usernameInput.setError(null);
        emailInput.setError(null);
        fullNameInput.setError(null);
        dateOfBirthInput.setError(null);

        // Store values
        String newUsername = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String fullName = fullNameInput.getText().toString().trim();
        String dateOfBirth = dateOfBirthInput.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username
        if (TextUtils.isEmpty(newUsername)) {
            usernameInput.setError("Username is required");
            focusView = usernameInput;
            cancel = true;
        } else if (newUsername.length() < 3) {
            usernameInput.setError("Username must be at least 3 characters");
            focusView = usernameInput;
            cancel = true;
        } else if (!originalUsername.equals(newUsername) && dbHelper.isUsernameTaken(newUsername)) {
            usernameInput.setError("Username already taken");
            focusView = usernameInput;
            cancel = true;
        }

        // Check for a valid full name
        if (TextUtils.isEmpty(fullName)) {
            fullNameInput.setError("Full name is required");
            focusView = fullNameInput;
            cancel = true;
        }

        // Check for a valid date of birth
        if (TextUtils.isEmpty(dateOfBirth)) {
            dateOfBirthInput.setError("Date of birth is required");
            focusView = dateOfBirthInput;
            cancel = true;
        }

        // Check for a valid email
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            focusView = emailInput;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailInput.setError("Enter a valid email address");
            focusView = emailInput;
            cancel = true;
        } else if (!email.equals(dbHelper.getUserEmail(originalUsername)) && dbHelper.isEmailRegistered(email)) {
            emailInput.setError("Email already registered");
            focusView = emailInput;
            cancel = true;
        }

        if (cancel) {
            // There was an error; focus the first form field with an error
            focusView.requestFocus();
        } else {
            // Update user profile
            boolean updated = dbHelper.updateUserProfile(originalUsername, newUsername, email, fullName, dateOfBirth);

            if (updated) {
                // Update shared preferences if username was changed
                if (!originalUsername.equals(newUsername)) {
                    SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                    editor.putString(KEY_USERNAME, newUsername);
                    editor.apply();
                }

                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish(); // Return to previous activity
            } else {
                Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back when up button is pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}