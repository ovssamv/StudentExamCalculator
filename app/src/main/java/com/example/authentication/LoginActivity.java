package com.example.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.DatabaseHelper;
import com.example.main.MainActivity;
import com.example.main.TeacherActivity;
import com.example.models.User;
import com.example.calculclau.R;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private CheckBox rememberMeCheckbox;
    private Button loginButton;

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize UI elements
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        rememberMeCheckbox = findViewById(R.id.remember_me_checkbox);
        loginButton = findViewById(R.id.login_button);


        // Check if user is already logged in
        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            usernameInput.setText(sharedPreferences.getString(KEY_USERNAME, ""));
            passwordInput.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
            rememberMeCheckbox.setChecked(true);
        }

        // Set up login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });


    }


    private void attemptLogin() {
        // Reset errors
        usernameInput.setError(null);
        passwordInput.setError(null);

        // Store values
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            focusView = passwordInput;
            cancel = true;
        }

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Username is required");
            focusView = usernameInput;
            cancel = true;
        }

        if (cancel) {
            // There was an error; focus the first form field with an error
            focusView.requestFocus();
        } else {
            // Perform the login attempt
            if (dbHelper.checkUser(username, password)) {
                if (rememberMeCheckbox.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_USERNAME, username);
                    editor.putString(KEY_PASSWORD, password);
                    editor.putBoolean(KEY_REMEMBER, true);
                    editor.putString("USER_TYPE", dbHelper.getUserType(username));
                    editor.putString("CUSTOM_ID", dbHelper.getUserCustomId(username));
                    editor.apply();
                } else {
                    // Clear only the remember-me related preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(KEY_PASSWORD);
                    editor.putBoolean(KEY_REMEMBER, false);
                    // Still store the username and user info for the session
                    editor.putString(KEY_USERNAME, username);
                    editor.putString("USER_TYPE", dbHelper.getUserType(username));
                    editor.putString("CUSTOM_ID", dbHelper.getUserCustomId(username));
                    editor.apply();
                }

                // Get user type
                String userType = dbHelper.getUserType(username);
                String customId = dbHelper.getUserCustomId(username);

                // Create a User object to pass to the next activity
                User user = new User();
                user.setUsername(username);
                user.setEmail(dbHelper.getUserEmail(username));
                user.setUserType(userType);
                user.setCustomId(customId);

                // Direct to appropriate activity based on user type
                if (DatabaseHelper.USER_TYPE_STUDENT.equals(userType)) {
                    // Navigate to student activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USER_TYPE", userType);
                    intent.putExtra("CUSTOM_ID", customId);
                    startActivity(intent);
                } else if (DatabaseHelper.USER_TYPE_TEACHER.equals(userType)) {
                    // Navigate to teacher activity
                    Intent intent = new Intent(LoginActivity.this, TeacherActivity.class);
                    intent.putExtra("USER_TYPE", userType);
                    intent.putExtra("CUSTOM_ID", customId);
                    startActivity(intent);
                } else if (DatabaseHelper.USER_TYPE_ADMIN.equals(userType)) {
                    // Navigate admin to signup page
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    intent.putExtra("USER_TYPE", userType);
                    intent.putExtra("CUSTOM_ID", customId);
                    startActivity(intent);
                }
                finish();
            } else {
                // Login failed
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}