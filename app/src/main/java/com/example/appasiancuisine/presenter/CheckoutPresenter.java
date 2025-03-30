package com.example.appasiancuisine.presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.appasiancuisine.data.dto.CheckoutDTO;
import com.example.appasiancuisine.data.dto.CheckoutItemDTO;
import com.example.appasiancuisine.utils.AppConfig;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CheckoutPresenter implements CheckoutContract.Presenter {

    private final CheckoutContract.View view;

    public CheckoutPresenter(CheckoutContract.View view) {
        this.view = view;
    }

    @Override
    public void checkout(String address, String phone, String note, String voucherCode,
                         List<CheckoutItemDTO> items, String accessToken) {

        CheckoutDTO dto = new CheckoutDTO(address, phone, note, voucherCode, items);
        Gson gson = new Gson();
        String jsonBody = gson.toJson(dto);

        Log.d("CheckoutPresenter", "📤 Sending checkout request...");
        Log.d("CheckoutPresenter", "📝 Body: " + jsonBody);
        Log.d("CheckoutPresenter", "🔐 Token: " + accessToken);

        new Thread(() -> {
            try {
                URL url = new URL(AppConfig.CHECKOUT_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                conn.setDoOutput(true);

                // Gửi JSON body
                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream() : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                conn.disconnect();

                String result = response.toString();
                Log.d("CheckoutPresenter", "📩 Response (" + responseCode + "): " + result);

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (responseCode >= 200 && responseCode < 300) {
                        Log.d("CheckoutPresenter", "✅ Checkout thành công");
                        view.onCheckoutSuccess("Đặt hàng thành công!");
                    } else {
                        Log.e("CheckoutPresenter", "❌ Checkout thất bại [" + responseCode + "]: " + result);
                        view.onCheckoutError("Lỗi: " + result);
                    }
                });

            } catch (Exception e) {
                Log.e("CheckoutPresenter", "❌ Exception: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() ->
                        view.onCheckoutError("Lỗi kết nối: " + e.getMessage()));
            }
        }).start();
    }
}
