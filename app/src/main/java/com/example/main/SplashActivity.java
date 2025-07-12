package com.example.main;

import static com.example.main.ProfileActivity.KEY_USERNAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.DatabaseHelper;
import com.example.authentication.LoginActivity;
import com.example.calculclau.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 3000; // 3 seconds
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        ImageView logoImage = findViewById(R.id.logo_image);
        logoImage.setImageResource(R.drawable.university_logo);

        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean(KEY_REMEMBER, false);

            Intent intent;
            if (isLoggedIn) {
                String username = sharedPreferences.getString(KEY_USERNAME, "");
                String userType = DatabaseHelper.getInstance(this).getUserType(username);

                if (DatabaseHelper.USER_TYPE_TEACHER.equals(userType)) {
                    intent = new Intent(this, TeacherActivity.class);
                } else {
                    intent = new Intent(this, MainActivity.class);
                }
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_TIMEOUT);
    }
}