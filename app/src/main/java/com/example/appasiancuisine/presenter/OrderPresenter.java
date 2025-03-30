package com.example.appasiancuisine.presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.appasiancuisine.data.dto.OrderDTO;
import com.example.appasiancuisine.utils.AppConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderPresenter implements OrderContract.Presenter {
    private final OrderContract.View view;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public OrderPresenter(OrderContract.View view) {
        this.view = view;
    }

    @Override
    public void getOrders(String token) {
        executorService.execute(() -> {
            List<OrderDTO> orderList = new ArrayList<>();
            HttpURLConnection conn = null;

            try {
                URL url = new URL(AppConfig.ORDER_LIST_URL);
                Log.d("API_CALL", "🔗 URL: " + url);
                Log.d("API_CALL", "📌 Token: " + (token != null ? token : "NULL"));

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json");

                int responseCode = conn.getResponseCode();
                Log.d("API_RESPONSE", "📡 Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d("API_RESPONSE", "📩 JSON nhận được: " + response.toString());

                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int id = jsonObject.optInt("id", -1);
                        String status = jsonObject.optString("status", "unknown");
                        double total = jsonObject.optDouble("total", 0.0);
                        String createdAt = jsonObject.optString("createdAt", "N/A");

                        OrderDTO order = new OrderDTO(id, status, total, createdAt);
                        orderList.add(order);
                    }

                    Log.d("API_RESPONSE", "✅ Tổng số đơn hàng: " + orderList.size());
                } else {
                    Log.e("API_ERROR", "❌ Lỗi API! Mã lỗi: " + responseCode);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    reader.close();
                    Log.e("API_ERROR", "⚠️ Nội dung lỗi: " + errorResponse.toString());

                    String errorMessage = "Lỗi API. Mã lỗi: " + responseCode + " - " + errorResponse.toString();
                    mainHandler.post(() -> view.showError(errorMessage));
                }
            } catch (Exception e) {
                Log.e("API_ERROR", "❌ Lỗi khi tải đơn hàng: " + e.getMessage(), e);
                mainHandler.post(() -> view.showError("Lỗi khi tải đơn hàng: " + e.getMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            mainHandler.post(() -> {
                try {
                    if (view != null) {
                        if (orderList.isEmpty()) {
                            Log.d("UI_UPDATE", "⚠️ Không có đơn hàng nào để hiển thị!");
                            view.showError("Không có đơn hàng nào!");
                        } else {
                            Log.d("UI_UPDATE", "✅ Hiển thị danh sách đơn hàng!");
                            view.showOrders(orderList);
                        }
                    }
                } catch (Exception e) {
                    Log.e("UI_UPDATE_ERROR", "❌ Lỗi khi cập nhật UI: " + e.getMessage(), e);
                }
            });
        });
    }
}
