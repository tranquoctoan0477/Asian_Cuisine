package com.example.appasiancuisine.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
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

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText editPhone;
    private Button buttonSendOtp;
    private View layoutOtp;

    private Button buttonVerifyOtp;
    private PreferenceManager pref;

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        pref = new PreferenceManager(this);

        editPhone = findViewById(R.id.edit_phone);
        buttonSendOtp = findViewById(R.id.button_send_otp);
        layoutOtp = findViewById(R.id.include_otp);
        buttonVerifyOtp = findViewById(R.id.button_verify_otp);

        // Gửi OTP
        buttonSendOtp.setOnClickListener(v -> {
            String phone = editPhone.getText().toString().trim();
            if (phone.isEmpty()) {
                editPhone.setError("Vui lòng nhập số điện thoại");
                return;
            }
            sendOtp(phone);
        });

        // Xác minh OTP
        buttonVerifyOtp.setOnClickListener(v -> {
            String phone = editPhone.getText().toString().trim();
            String otp = otp1.getText().toString().trim()
                    + otp2.getText().toString().trim()
                    + otp3.getText().toString().trim()
                    + otp4.getText().toString().trim()
                    + otp5.getText().toString().trim()
                    + otp6.getText().toString().trim();

            if (otp.length() != 6) {
                Toast.makeText(this, "Vui lòng nhập đủ 6 chữ số OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyOtp(phone, otp);
        });
    }

    private void sendOtp(String phone) {
        new Thread(() -> {
            try {
                URL url = new URL(AppConfig.SEND_OTP_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("phoneNumber", phone);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "✅ Gửi OTP thành công", Toast.LENGTH_SHORT).show();
                        layoutOtp.setVisibility(View.VISIBLE);
                        initOtpInputs(); // setup auto focus
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "❌ Gửi OTP thất bại", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi gửi OTP", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void verifyOtp(String phone, String otp) {
        new Thread(() -> {
            try {
                URL url = new URL(AppConfig.VERIFY_OTP_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("phoneNumber", phone);
                json.put("otp", otp);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "✅ OTP hợp lệ", Toast.LENGTH_SHORT).show();
                        pref.saveResetPhone(phone);
                        Intent intent = new Intent(this, ResetNewPasswordActivity.class);
                        startActivity(intent);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "❌ OTP không đúng", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Lỗi xác minh OTP", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Khởi tạo các ô OTP và setup auto focus
    private void initOtpInputs() {
        otp1 = layoutOtp.findViewById(R.id.otp_digit_1);
        otp2 = layoutOtp.findViewById(R.id.otp_digit_2);
        otp3 = layoutOtp.findViewById(R.id.otp_digit_3);
        otp4 = layoutOtp.findViewById(R.id.otp_digit_4);
        otp5 = layoutOtp.findViewById(R.id.otp_digit_5);
        otp6 = layoutOtp.findViewById(R.id.otp_digit_6);

        setupOtpInputBehaviour(otp1, otp2);
        setupOtpInputBehaviour(otp2, otp3);
        setupOtpInputBehaviour(otp3, otp4);
        setupOtpInputBehaviour(otp4, otp5);
        setupOtpInputBehaviour(otp5, otp6);
        setupOtpInputBehaviour(otp6, null); // ô cuối không cần next

    }

    private void setupOtpAutoMove(EditText current, EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    next.requestFocus();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupOtpInputBehaviour(EditText current, EditText next) {
        current.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (current.getText().toString().isEmpty()) {
                    // Nếu ô hiện tại rỗng → quay về ô trước và xoá nội dung
                    View prev = current.focusSearch(View.FOCUS_LEFT);
                    if (prev != null && prev instanceof EditText) {
                        EditText prevEdit = (EditText) prev;
                        prevEdit.setText("");         // Xoá nội dung ô trước
                        prevEdit.requestFocus();      // Đưa focus về
                        return true;
                    }
                }
            }
            return false;
        });

        current.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && next != null) {
                    next.requestFocus();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }


}
