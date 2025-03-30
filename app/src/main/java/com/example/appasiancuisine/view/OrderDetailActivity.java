package com.example.appasiancuisine.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.OrderDetailAdapter;
import com.example.appasiancuisine.data.dto.OrderItemDTO;
import com.example.appasiancuisine.utils.AppConfig;
import com.example.appasiancuisine.utils.PreferenceManager;

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

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderDetailAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String TAG = "ORDER_DETAIL_API";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        recyclerView = findViewById(R.id.recycler_order_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        int orderId = intent.getIntExtra("ORDER_ID", -1);

        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy ID đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d(TAG, "🟢 Đã nhận được Order ID: " + orderId);

        PreferenceManager preferenceManager = new PreferenceManager(this);
        String token = preferenceManager.getAccessToken();
        Log.d(TAG, "🔑 Token lấy từ PreferenceManager: " + token);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "❌ Token không hợp lệ hoặc trống!");
            finish();
            return;
        }

        fetchOrderDetails(orderId, token);
    }

    private void fetchOrderDetails(int orderId, String token) {
        executorService.execute(() -> {
            try {
                String apiUrl = AppConfig.ORDER_DETAIL_URL + orderId + "/items";
                Log.d(TAG, "🔗 URL đầy đủ để gọi API: " + apiUrl);

                URL url = new URL(apiUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "📡 Mã phản hồi từ server: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d(TAG, "📩 JSON nhận được: " + response.toString());
                    runOnUiThread(() -> parseOrderDetails(response.toString()));
                } else {
                    Log.e(TAG, "❌ Lỗi khi gọi API - Mã lỗi: " + responseCode);
                    runOnUiThread(() -> Toast.makeText(OrderDetailActivity.this, "Lỗi khi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show());
                }

                connection.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "❌ Lỗi khi gọi API: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(OrderDetailActivity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void parseOrderDetails(String response) {
        try {
            Log.d("ORDER_DETAIL_API", "📩 JSON Nhận được: " + response); // Log JSON nhận được

            // Parse thẳng từ chuỗi JSON thành JSONArray
            JSONArray itemsArray = new JSONArray(response);  // ✅ Đổi từ JSONObject sang JSONArray

            List<OrderItemDTO> orderItemList = new ArrayList<>();

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObject = itemsArray.getJSONObject(i);

                int productId = itemObject.getInt("productId");
                String productName = itemObject.getString("productName");
                int quantity = itemObject.getInt("quantity");
                double price = itemObject.getDouble("price");
                String note = itemObject.optString("note", "");
                String mainImg = itemObject.optString("mainImg", "");

                // Kiểm tra URL ảnh hợp lệ
                String imageUrl;
                if (mainImg.startsWith("http")) {
                    imageUrl = mainImg;
                } else {
                    imageUrl = AppConfig.BASE_URL + mainImg;
                }

                OrderItemDTO orderItem = new OrderItemDTO(productId, productName, quantity, price, note, imageUrl);
                orderItemList.add(orderItem);
            }

            // Cập nhật UI trên main thread
            runOnUiThread(() -> {
                if (orderItemList.isEmpty()) {
                    Toast.makeText(this, "Không có sản phẩm nào trong đơn hàng này", Toast.LENGTH_SHORT).show();
                } else {
                    adapter = new OrderDetailAdapter(OrderDetailActivity.this, orderItemList);
                    recyclerView.setAdapter(adapter);
                }
            });

        } catch (Exception e) {
            Log.e("ORDER_DETAIL_PARSE", "❌ Lỗi khi parse dữ liệu: " + e.getMessage(), e);
            runOnUiThread(() -> Toast.makeText(this, "Lỗi khi hiển thị chi tiết đơn hàng", Toast.LENGTH_SHORT).show());
        }
    }


}
