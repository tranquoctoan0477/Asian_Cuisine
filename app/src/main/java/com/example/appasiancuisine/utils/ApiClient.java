package com.example.appasiancuisine.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    public static JSONObject login(String email, String password) throws Exception {
        System.out.println("➡️ Bắt đầu gọi API đăng nhập...");

        URL url = new URL(AppConfig.LOGIN_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Gửi dữ liệu login
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", email);
        requestBody.put("password", password);

        System.out.println("📤 Request body: " + requestBody.toString());

        OutputStream os = conn.getOutputStream();
        os.write(requestBody.toString().getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        System.out.println("📥 Response Code: " + responseCode);

        // Đọc response
        BufferedReader reader;
        if (responseCode == HttpURLConnection.HTTP_OK) {
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

        System.out.println("📥 Response Body: " + response.toString());

        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("✅ Đăng nhập thành công!");
            return new JSONObject(response.toString());
        } else {
            System.out.println("❌ Đăng nhập thất bại!");
            throw new Exception("Login failed: " + response.toString());
        }
    }

    public static JSONObject register(String username, String email, String password, String phone, String gender) throws Exception {
        System.out.println("➡️ Bắt đầu gọi API đăng ký...");

        URL url = new URL(AppConfig.REGISTER_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("phone", phone);
        requestBody.put("gender", gender);

        System.out.println("📤 Request body: " + requestBody.toString());

        OutputStream os = conn.getOutputStream();
        os.write(requestBody.toString().getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        System.out.println("📥 Response Code: " + responseCode);

        BufferedReader reader;
        if (responseCode == HttpURLConnection.HTTP_OK) {
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

        System.out.println("📥 Response Body: " + response.toString());

        if (responseCode == HttpURLConnection.HTTP_OK) {
            return new JSONObject(response.toString());
        } else {
            // 👉 Ném lỗi để bên RegisterPresenter bắt được và gọi onRegisterFailure()
            JSONObject errorJson = new JSONObject(response.toString());
            String errorMessage = errorJson.has("message") ? errorJson.getString("message") : "Đăng ký thất bại!";
            throw new Exception(errorMessage);
        }
    }


}
