package com.example.appasiancuisine.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.CartAdapter;
import com.example.appasiancuisine.data.dto.CheckoutItemDTO;
import com.example.appasiancuisine.utils.AppConfig;
import com.example.appasiancuisine.utils.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartUpdateListener  {

    private RecyclerView recyclerCartItems;
    private TextView textCartTotal;
    private CartAdapter cartAdapter;
    private List<JSONObject> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerCartItems = findViewById(R.id.recycler_cart_items);
        textCartTotal = findViewById(R.id.text_cart_total);
        Button  buttonCheckout = findViewById(R.id.button_checkout); // ✅ Ánh xạ nút Thanh toán

        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItems, this);
        recyclerCartItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerCartItems.setAdapter(cartAdapter);

        fetchCartItems();

        // ✅ Xử lý khi bấm "Thanh toán"
        buttonCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<CheckoutItemDTO> checkoutList = convertToCheckoutItems(cartItems);

            // Log toàn bộ danh sách `checkoutList` trước khi gửi đi
            for (CheckoutItemDTO item : checkoutList) {
                Log.d("CartActivity", "📤 Đang truyền - Product: "
                        + item.getProductName() + " - Note: " + item.getNote());
            }

            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            intent.putExtra("checkout_items", (Serializable) checkoutList);
            startActivity(intent);
        });

    }

    private void fetchCartItems() {
        String url = AppConfig.CART_GET_URL;

        Log.d("CartActivity", "🔥 Gửi yêu cầu GET tới URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("CartActivity", "📥 Response từ server: " + response.toString());

                        JSONArray itemsArray = response.getJSONArray("items");

                        cartItems.clear();
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            // Cập nhật thêm thông tin ghi chú
                            if (!item.has("note")) {
                                item.put("note", "");  // Nếu không có note, set thành chuỗi rỗng
                            }
                            cartItems.add(item);
                        }
                        cartAdapter.notifyDataSetChanged();

                        // ✅ Cập nhật tổng giá giỏ hàng bằng hàm updateTotalAfterRemove
                        updateTotalAfterRemove();

                        Log.d("CartActivity", "📥 Tải giỏ hàng thành công, số lượng sản phẩm: " + cartItems.size());
                    } catch (JSONException e) {
                        Log.e("CartActivity", "❌ JSON Parsing Error: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý dữ liệu từ server!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("CartActivity", "❌ Lỗi khi tải giỏ hàng: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("CartActivity", "❌ Mã lỗi HTTP: " + error.networkResponse.statusCode);
                        try {
                            String errorResponse = new String(error.networkResponse.data);
                            Log.e("CartActivity", "❌ Phản hồi lỗi từ server: " + errorResponse);
                        } catch (Exception e) {
                            Log.e("CartActivity", "❌ Không thể đọc phản hồi lỗi từ server.");
                        }
                    }
                    Toast.makeText(this, "Lỗi khi tải giỏ hàng!", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                PreferenceManager preferenceManager = new PreferenceManager(CartActivity.this);
                String accessToken = preferenceManager.getAccessToken();

                if (accessToken == null || accessToken.isEmpty()) {
                    Log.e("CartActivity", "❌ AccessToken không tồn tại. Bạn cần đăng nhập.");
                    Toast.makeText(CartActivity.this, "Bạn cần đăng nhập trước khi xem giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    headers.put("Authorization", "Bearer " + accessToken);
                    Log.d("CartActivity", "✅ AccessToken được thêm vào header: " + accessToken);
                }

                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public void updateCartNote(long productId, String note, int quantity) {
        String url = AppConfig.CART_UPDATE_URL; // URL của API update giỏ hàng

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("productId", productId);
            requestBody.put("note", note);
            requestBody.put("quantity", quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                response -> {
                    Toast.makeText(CartActivity.this, "Ghi chú được cập nhật thành công!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(CartActivity.this, "Lỗi khi cập nhật ghi chú!", Toast.LENGTH_SHORT).show();
                    Log.e("CartActivity", "❌ Lỗi khi cập nhật ghi chú: " + error.toString());
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                PreferenceManager preferenceManager = new PreferenceManager(CartActivity.this);
                String accessToken = preferenceManager.getAccessToken();

                if (accessToken != null && !accessToken.isEmpty()) {
                    headers.put("Authorization", "Bearer " + accessToken);
                }

                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }



    // Cập nhật tổng giá sau khi xóa một item
    public void updateTotalAfterRemove() {
        double total = 0;
        for (JSONObject item : cartItems) {
            try {
                int quantity = item.getInt("quantity");
                double price = item.getDouble("price");
                total += price * quantity;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // ✅ Hiển thị tổng giá theo đúng định dạng tiền Việt Nam
        textCartTotal.setText(String.format("%,d₫", (int) total));
    }

    private List<CheckoutItemDTO> convertToCheckoutItems(List<JSONObject> jsonItems) {
        List<CheckoutItemDTO> result = new ArrayList<>();
        for (JSONObject obj : jsonItems) {
            try {
                long productId = obj.getLong("productId");
                String productName = obj.getString("productName");
                int quantity = obj.getInt("quantity");
                double price = obj.getDouble("price");

                // ✅ Kiểm tra dữ liệu `note` xem có bị `null` hay không
                String note = obj.optString("note", ""); // Dùng `optString` để đảm bảo không bị lỗi nếu note không tồn tại
                if (note == null || note.equals("null")) {
                    note = ""; // Nếu là `null`, thay bằng chuỗi rỗng
                }

                String thumbnail = obj.optString("thumbnail", "");

                Log.d("CartActivity", "📦 Product: " + productName + " - Note: " + note);

                CheckoutItemDTO item = new CheckoutItemDTO(productId, productName, quantity, note, price, thumbnail);
                result.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }





}
