package com.example.appasiancuisine.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.presenter.LoginContract;
import com.example.appasiancuisine.presenter.LoginPresenter;
import com.example.appasiancuisine.utils.PreferenceManager;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textGoToRegister;
    private ImageView iconFingerprint;

    private LoginPresenter presenter;
    private ProgressDialog progressDialog;

    private PreferenceManager pref;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textGoToRegister = findViewById(R.id.textGoToRegister);
        iconFingerprint = findViewById(R.id.iconFingerprint); // Icon để login bằng vân tay

        pref = new PreferenceManager(this);

        presenter = new LoginPresenter(this, pref);

        // Nếu có email được lưu, tự động điền
        String savedEmail = pref.getEmail();
        if (savedEmail != null) {
            editTextEmail.setText(savedEmail);
        }

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

        setupBiometricLogin(); // Setup đăng nhập bằng vân tay

        iconFingerprint.setOnClickListener(v -> { // Sự kiện click vào icon vân tay
            if (pref.isFingerprintEnabled()) { // Chỉ hiển thị nếu vân tay đã được bật
                biometricPrompt.authenticate(promptInfo);
            } else {
                Toast.makeText(this, "Bạn chưa kích hoạt đăng nhập bằng vân tay.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBiometricLogin() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Toast.makeText(LoginActivity.this, "Xác thực vân tay thành công!", Toast.LENGTH_SHORT).show();

                // Thực hiện đăng nhập tự động
                String savedEmail = pref.getEmail();
                String accessToken = pref.getAccessToken();
                String refreshToken = pref.getRefreshToken();
                String username = pref.getUsername();
                String role = pref.getRole();

                if (savedEmail != null && accessToken != null) {
                    onLoginSuccess(accessToken, refreshToken, username, savedEmail, role);
                } else {
                    Toast.makeText(LoginActivity.this, "Không tìm thấy thông tin đăng nhập. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, "Xác thực không thành công. Thử lại.", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng nhập bằng vân tay")
                .setDescription("Sử dụng vân tay đã được đăng ký để đăng nhập.")
                .setNegativeButtonText("Hủy")
                .build();
    }

    @Override
    public void onLoginSuccess(String accessToken, String refreshToken, String username, String email, String role) {
        Toast.makeText(this, "Đăng nhập thành công!\nXin chào " + username, Toast.LENGTH_SHORT).show();

        // Lưu thông tin đăng nhập
        pref.saveLoginData(accessToken, refreshToken, username, email, role);

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
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
