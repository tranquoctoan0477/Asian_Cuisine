package com.example.appasiancuisine.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.presenter.LoginContract;
import com.example.appasiancuisine.presenter.LoginPresenter;
import com.example.appasiancuisine.utils.PreferenceManager;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textGoToRegister;

    private LoginPresenter presenter;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail); // đúng ID ở layout
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textGoToRegister = findViewById(R.id.textGoToRegister);

        // ✅ Tự động điền email nếu có
        PreferenceManager pref = new PreferenceManager(this);
        String emailFromIntent = getIntent().getStringExtra("email");
        if (emailFromIntent != null && !emailFromIntent.isEmpty()) {
            editTextEmail.setText(emailFromIntent);
        } else {
            String savedEmail = pref.getEmail();
            if (savedEmail != null) {
                editTextEmail.setText(savedEmail);
            }
        }

        presenter = new LoginPresenter(this, new PreferenceManager(this));

        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError("Vui lòng nhập email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Vui lòng nhập mật khẩu");
                return;
            }

            presenter.login(email, password);
        });

        textGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    @Override
    public void onLoginSuccess(String accessToken, String refreshToken, String username, String email, String role) {
        Toast.makeText(this, "Đăng nhập thành công!\nXin chào " + username, Toast.LENGTH_SHORT).show();

        // In ra log kiểm tra dữ liệu đã lưu
        PreferenceManager pref = new PreferenceManager(this);

        Log.d("DEBUG_PREF", "AccessToken: " + pref.getAccessToken());
        Log.d("DEBUG_PREF", "RefreshToken: " + pref.getRefreshToken());
        Log.d("DEBUG_PREF", "Username: " + pref.getUsername());
        Log.d("DEBUG_PREF", "Email: " + pref.getEmail());
        Log.d("DEBUG_PREF", "Role: " + pref.getRole());

        // Chuyển sang màn hình chính
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginFailure(String message) {
        Toast.makeText(this, "Đăng nhập thất bại: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.setCancelable(false); // không cho người dùng tắt dialog
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
