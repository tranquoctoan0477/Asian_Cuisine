package com.example.appasiancuisine.presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.appasiancuisine.utils.ApiClient;
import com.example.appasiancuisine.utils.AppConfig;
import com.example.appasiancuisine.utils.PreferenceManager;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View view;
    private final PreferenceManager preferenceManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public LoginPresenter(LoginContract.View view, PreferenceManager preferenceManager) {
        this.view = view;
        this.preferenceManager = preferenceManager;
    }


    @Override
    public void login(String email, String password) {
        view.showLoading();

        executorService.execute(() -> {
            try {
                URL url = new URL(AppConfig.LOGIN_URL);
                Log.d("DEBUG_LOGIN", "URL: " + url); // ✅ Log URL gửi lên server

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject requestBody = new JSONObject();
                requestBody.put("email", email);
                requestBody.put("password", password);

                Log.d("DEBUG_LOGIN", "Sending request: " + requestBody.toString());

                OutputStream os = conn.getOutputStream();
                os.write(requestBody.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("DEBUG_LOGIN", "Response code: " + responseCode);

                BufferedReader reader;
                if (responseCode == 200) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                conn.disconnect();

                Log.d("DEBUG_LOGIN", "Response body: " + response.toString());

                if (responseCode == 200) {
                    JSONObject json = new JSONObject(response.toString());

                    String accessToken = json.getString("accessToken");
                    String refreshToken = json.getString("refreshToken");
                    String username = json.getString("username");
                    String emailResponse = json.getString("email");
                    String role = json.getString("role");

                    preferenceManager.saveLoginData(accessToken, refreshToken, username, emailResponse, role);

                    Log.d("DEBUG_PREF", "AccessToken: " + preferenceManager.getAccessToken());
                    Log.d("DEBUG_PREF", "RefreshToken: " + preferenceManager.getRefreshToken());
                    Log.d("DEBUG_PREF", "Username: " + preferenceManager.getUsername());
                    Log.d("DEBUG_PREF", "Email: " + preferenceManager.getEmail());
                    Log.d("DEBUG_PREF", "Role: " + preferenceManager.getRole());

                    mainHandler.post(() -> {
                        view.hideLoading();
                        view.onLoginSuccess(accessToken, refreshToken, username, emailResponse, role);
                    });
                } else {
                    try {
                        JSONObject json = new JSONObject(response.toString());
                        String message = json.has("message") ? json.getString("message") : "Login failed";

                        mainHandler.post(() -> {
                            view.hideLoading();
                            view.onLoginFailure(message);
                        });
                    } catch (Exception e) {
                        Log.e("DEBUG_LOGIN", "Không thể parse JSON: " + response.toString());
                        mainHandler.post(() -> {
                            view.hideLoading();
                            view.onLoginFailure("Không thể đọc phản hồi từ server.");
                        });
                    }
                }

            } catch (Exception e) {
                Log.e("DEBUG_LOGIN", "Error: ", e);
                mainHandler.post(() -> {
                    view.hideLoading();
                    view.onLoginFailure("Đã xảy ra lỗi: " + e.getMessage());
                });
            }
        });
    }

}
