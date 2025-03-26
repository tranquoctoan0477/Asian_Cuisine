package com.example.appasiancuisine.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.presenter.RegisterContract;
import com.example.appasiancuisine.presenter.RegisterPresenter;

public class RegisterActivity extends AppCompatActivity implements RegisterContract.View {

    private EditText editTextUsername, editTextEmail, editTextPassword, editTextPhone,editTextConfirmPassword ;
    private RadioGroup genderGroup;
    private RadioButton radioMale, radioFemale;
    private Button buttonRegister;
    private TextView textGoToLogin;

    private RegisterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextconfirmPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        genderGroup = findViewById(R.id.genderGroup);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        buttonRegister = findViewById(R.id.buttonRegister);
        textGoToLogin = findViewById(R.id.textGoToLogin);

        presenter = new RegisterPresenter(this);

        // Xử lý khi bấm nút đăng ký
        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            // Kiểm tra dữ liệu rỗng
            if (TextUtils.isEmpty(username)) {
                editTextUsername.setError("Vui lòng nhập tên đăng nhập");
                return;
            }

            if (!username.matches("^[a-zA-Z0-9_À-ỹ\\s]{3,30}$")) {
                editTextUsername.setError("Tên không chứa ký tự đặc biệt và phải từ 3–30 ký tự");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError("Vui lòng nhập email");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Email không hợp lệ");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Vui lòng nhập mật khẩu");
                return;
            }

            if (password.length() < 6) {
                editTextPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
                return;
            }

            if (TextUtils.isEmpty(phone)) {
                editTextPhone.setError("Vui lòng nhập số điện thoại");
                return;
            }

            if (!phone.matches("^\\d{9,11}$")) {
                editTextPhone.setError("Số điện thoại không hợp lệ (9-11 chữ số)");
                return;
            }
            if (!password.equals(confirmPassword)) {
                editTextConfirmPassword.setError("Mật khẩu không khớp");
                return;
            }
            // Lấy giới tính
            int genderId = genderGroup.getCheckedRadioButtonId();
            if (genderId == -1) {
                Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = (genderId == R.id.radioMale) ? "Nam" : "Nữ";

            // Gọi presenter để thực hiện đăng ký
            presenter.register(username, email, password, phone, gender);
        });

        // Điều hướng sang login
        textGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    public void onRegisterSuccess(String message) {
        Toast.makeText(this, "🎉 " + message, Toast.LENGTH_SHORT).show();

        // ✅ Gửi email vừa đăng ký sang LoginActivity
        String email = editTextEmail.getText().toString().trim();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegisterFailure(String message) {
        Toast.makeText(this, "❌ Lỗi: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading() {
        Toast.makeText(this, "⏳ Đang xử lý đăng ký...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoading() {
        // Có thể dùng để ẩn ProgressDialog nếu sau này bạn có thêm
    }
}
