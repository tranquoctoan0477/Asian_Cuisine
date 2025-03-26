package com.example.appasiancuisine.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.utils.PreferenceManager;

public class AuthChoiceActivity extends AppCompatActivity {

    Button buttonSignUp, buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_choice);

        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSignIn = findViewById(R.id.buttonSignIn);

        buttonSignUp.setOnClickListener(v -> {
            startActivity(new Intent(AuthChoiceActivity.this, RegisterActivity.class));
        });

        // Chuyển sang LoginActivity
        buttonSignIn.setOnClickListener(v -> {
            PreferenceManager pref = new PreferenceManager(AuthChoiceActivity.this);
            String accessToken = pref.getAccessToken();

            if (accessToken != null && !accessToken.isEmpty()) {
                // ✅ Nếu đã có token → bỏ qua LoginActivity → vào MainActivity luôn
                Intent intent = new Intent(AuthChoiceActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // không cho quay lại màn chọn Auth
            } else {
                // ❌ Nếu chưa đăng nhập → vào LoginActivity
                startActivity(new Intent(AuthChoiceActivity.this, LoginActivity.class));
            }
        });
    }
}
