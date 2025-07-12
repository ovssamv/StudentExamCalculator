package com.example.authentication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.DatabaseHelper;
import com.example.calculclau.R;
import com.example.models.Student;

import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private EditText customIdInput;
    private EditText fullNameInput;
    private EditText dateOfBirthInput;
    private RadioGroup userTypeRadioGroup;
    private Button signupButton;
    private TextView loginLink;
    private DatabaseHelper dbHelper;
    private TextView customIdLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // Initialize UI elements
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        customIdInput = findViewById(R.id.custom_id_input);
        customIdLabel = findViewById(R.id.custom_id_label);
        fullNameInput = findViewById(R.id.full_name_input);
        dateOfBirthInput = findViewById(R.id.date_of_birth_input);
        userTypeRadioGroup = findViewById(R.id.user_type_radio_group);
        signupButton = findViewById(R.id.signup_button);
        loginLink = findViewById(R.id.login_link);

        // Set up date picker for date of birth
        dateOfBirthInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set up radio button listener to change label text
        userTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_student) {
                    customIdLabel.setText("Student ID:");
                } else if (checkedId == R.id.radio_teacher) {
                    customIdLabel.setText("Teacher ID:");
                }
            }
        });

        // Set up signup button click listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignup();
            }
        });

        // Set up login link click listener
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start LoginActivity explicitly
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
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
                year - 18, // Default to 18 years ago
                month,
                day);
        datePickerDialog.show();
    }

    private void attemptSignup() {
        // Reset errors
        usernameInput.setError(null);
        emailInput.setError(null);
        passwordInput.setError(null);
        confirmPasswordInput.setError(null);
        customIdInput.setError(null);
        fullNameInput.setError(null);
        dateOfBirthInput.setError(null);

        // Store values
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String customId = customIdInput.getText().toString().trim();
        String fullName = fullNameInput.getText().toString().trim();
        String dateOfBirth = dateOfBirthInput.getText().toString().trim();

        // Get user type
        int selectedRadioButtonId = userTypeRadioGroup.getCheckedRadioButtonId();
        String userType;
        if (selectedRadioButtonId == R.id.radio_student) {
            userType = DatabaseHelper.USER_TYPE_STUDENT;
        } else if (selectedRadioButtonId == R.id.radio_teacher) {
            userType = DatabaseHelper.USER_TYPE_TEACHER;
        } else {
            // No radio button selected
            Toast.makeText(SignupActivity.this, "Please select a user type", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Username is required");
            focusView = usernameInput;
            cancel = true;
        } else if (username.length() < 3) {
            usernameInput.setError("Username must be at least 3 characters");
            focusView = usernameInput;
            cancel = true;
        } else if (dbHelper.isUsernameTaken(username)) {
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
        } else if (dbHelper.isEmailRegistered(email)) {
            emailInput.setError("Email already registered");
            focusView = emailInput;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            focusView = passwordInput;
            cancel = true;
        } else if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            focusView = passwordInput;
            cancel = true;
        }

        // Check if passwords match
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInput.setError("Confirm your password");
            focusView = confirmPasswordInput;
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            focusView = confirmPasswordInput;
            cancel = true;
        }

        // Check for a valid custom ID
        if (TextUtils.isEmpty(customId)) {
            customIdInput.setError("ID is required");
            focusView = customIdInput;
            cancel = true;
        } else if (dbHelper.isCustomIdTaken(customId, userType)) {
            customIdInput.setError("This ID is already registered");
            focusView = customIdInput;
            cancel = true;
        }

        if (cancel) {
            // There was an error; focus the first form field with an error
            focusView.requestFocus();
        } else {
            // Register the user in the users table with full name and date of birth
            boolean registrationSuccessful = dbHelper.registerUser(username, email, password, userType, customId, fullName, dateOfBirth);

            if (registrationSuccessful) {
                // If the user is a student, also add to the students table
                if (userType.equals(DatabaseHelper.USER_TYPE_STUDENT)) {
                    Student student = new Student(customId, username, email);
                    boolean studentAdded = dbHelper.addStudent(student);

                    if (!studentAdded) {
                        Toast.makeText(SignupActivity.this, "User registered but failed to add student record",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Toast.makeText(SignupActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                
                // Clear the form
                usernameInput.setText("");
                emailInput.setText("");
                passwordInput.setText("");
                confirmPasswordInput.setText("");
                customIdInput.setText("");
                fullNameInput.setText("");
                dateOfBirthInput.setText("");
                userTypeRadioGroup.clearCheck();
                
            } else {
                Toast.makeText(SignupActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}