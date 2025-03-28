package com.example.appasiancuisine.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.utils.AppConfig;
import com.example.appasiancuisine.utils.PreferenceManager;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResetNewPasswordActivity extends AppCompatActivity {

    private EditText editNewPassword, editConfirmPassword;
    private Button buttonResetPassword;
    private PreferenceManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_reset_new_password); // 👈 đổi đúng tên layout nếu cần

        pref = new PreferenceManager(this);

        editNewPassword = findViewById(R.id.edit_new_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);
        buttonResetPassword = findViewById(R.id.button_reset_password);

        buttonResetPassword.setOnClickListener(v -> {
            String newPassword = editNewPassword.getText().toString().trim();
            String confirmPassword = editConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không trùng khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            String phone = pref.getResetPhone();
            if (phone == null || phone.isEmpty()) {
                Toast.makeText(this, "Lỗi: thiếu số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }

            updatePassword(phone, newPassword);
        });
    }

    private void updatePassword(String phone, String newPassword) {
        new Thread(() -> {
            try {
                URL url = new URL(AppConfig.RESET_PASSWORD_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String confirmPassword = editConfirmPassword.getText().toString().trim(); // 👈 lấy thêm dòng này

                JSONObject json = new JSONObject();
                json.put("phoneNumber", phone);
                json.put("newPassword", newPassword);
                json.put("confirmPassword", confirmPassword); // 👈 gửi luôn confirmPassword

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "✅ Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        pref.clearResetPhone(); // Xoá tạm số điện thoại đã dùng
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "❌ Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "⚠️ Lỗi khi đổi mật khẩu", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}
