package com.example.appasiancuisine.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 2 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PreferenceManager prefManager = new PreferenceManager(this);

        new Handler().postDelayed(() -> {
            if (prefManager.isFirstTimeLaunch()) {
                // Lần đầu mở app → vào Intro slider
                startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            } else {
                // Mở lại → vào AuthChoiceActivity
                startActivity(new Intent(SplashActivity.this, AuthChoiceActivity.class));
            }
            finish();
        }, SPLASH_DURATION);
    }
}
