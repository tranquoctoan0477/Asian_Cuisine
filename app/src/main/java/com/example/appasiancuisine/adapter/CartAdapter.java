package com.example.appasiancuisine.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appasiancuisine.R;
import com.example.appasiancuisine.utils.AppConfig;
import com.example.appasiancuisine.utils.PreferenceManager;
import com.example.appasiancuisine.view.CartActivity;
import com.example.appasiancuisine.view.CartUpdateListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<JSONObject> cartItems;
    private final CartUpdateListener cartUpdateListener;

    public CartAdapter(Context context, List<JSONObject> cartItems, CartUpdateListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartUpdateListener = listener; // Gán Interface
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for each item in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        JSONObject item = cartItems.get(position);

        try {
            String productId = item.getString("productId");
            String productName = item.getString("productName");
            int quantity = item.getInt("quantity");
            double price = item.getDouble("price");
            String note = item.optString("note", "");

            holder.textProductName.setText(productName);
            holder.textQuantity.setText(String.valueOf(quantity));
            holder.textPrice.setText(String.format("%,d₫", (int) price));
            holder.textProductNote.setText(note);

            String imageUrl = item.optString("thumbnail", "");
            if (!imageUrl.isEmpty()) {
                String fullImageUrl = AppConfig.BASE_URL + imageUrl;
                Picasso.get()
                        .load(fullImageUrl)
                        .placeholder(R.drawable.pd10)
                        .error(R.drawable.pd10)
                        .into(holder.imageProduct);
            }

            // Thêm TextWatcher để cập nhật `note` ngay khi người dùng nhập
            holder.textProductNote.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        String updatedNote = s.toString();
                        item.put("note", updatedNote); // ✅ Cập nhật ngay lập tức `note` vào `cartItems`
                        Log.d("CartAdapter", "📥 Đang cập nhật ghi chú: " + updatedNote);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            holder.buttonRemove.setOnClickListener(v -> removeCartItem(productId, position));

            holder.buttonIncrease.setOnClickListener(v -> updateQuantity(productId, quantity + 1, holder.textProductNote.getText().toString(), position));

            holder.buttonDecrease.setOnClickListener(v -> {
                if (quantity > 1) {
                    updateQuantity(productId, quantity - 1, holder.textProductNote.getText().toString(), position);
                } else {
                    Toast.makeText(context, "Không thể giảm số lượng dưới 1!", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void removeCartItem(String productId, int position) {
        String url = AppConfig.CART_REMOVE_URL + "/" + productId; // URL hợp lệ

        Log.d("CartAdapter", "🔥 Gửi yêu cầu DELETE tới URL: " + url); // Log URL để kiểm tra

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString("message");

                        cartItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartItems.size());

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        if (cartUpdateListener != null) {
                            cartUpdateListener.updateTotalAfterRemove();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Lỗi xử lý phản hồi từ server!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("CartAdapter", "❌ Lỗi kết nối đến server: " + error.toString());
                    Toast.makeText(context, "Lỗi kết nối đến server!", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                PreferenceManager preferenceManager = new PreferenceManager(context);
                String accessToken = preferenceManager.getAccessToken();

                if (accessToken != null && !accessToken.isEmpty()) {
                    headers.put("Authorization", "Bearer " + accessToken);
                    Log.d("CartAdapter", "✅ Gửi Token: " + accessToken); // Log kiểm tra token
                } else {
                    Log.e("CartAdapter", "❌ Token không tồn tại hoặc không hợp lệ.");
                }

                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    private void updateQuantity(String productId, int quantity, String note, int position) {
        String url = AppConfig.CART_UPDATE_URL;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("productId", productId); // ✅ Lưu ý: Không truyền dưới dạng String
            requestBody.put("quantity", quantity);
            requestBody.put("note", note);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                response -> {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        // ✅ Cập nhật quantity mới trong cartItems
                        cartItems.get(position).put("quantity", quantity);
                        cartItems.get(position).put("note", note);
                        notifyItemChanged(position);

                        // Cập nhật tổng giá (gọi từ CartActivity)
                        if (cartUpdateListener != null) {
                            cartUpdateListener.updateTotalAfterRemove(); // Gọi trực tiếp từ Interface thay vì context
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Lỗi xử lý phản hồi từ server!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Lỗi kết nối đến server!", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                PreferenceManager preferenceManager = new PreferenceManager(context);
                String accessToken = preferenceManager.getAccessToken();
                if (accessToken != null && !accessToken.isEmpty()) {
                    headers.put("Authorization", "Bearer " + accessToken);
                }

                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }



    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textProductName, textQuantity, textPrice;
        ImageView imageProduct;
        ImageButton buttonRemove, buttonIncrease, buttonDecrease; // ✅ Thêm Increase, Decrease
        EditText textProductNote; // ✅ Thêm EditText cho note

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            // Khởi tạo tất cả các view trong layout
            textProductName = itemView.findViewById(R.id.text_product_name);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            textPrice = itemView.findViewById(R.id.text_product_price);
            imageProduct = itemView.findViewById(R.id.image_product);
            buttonRemove = itemView.findViewById(R.id.button_remove);

            // ✅ Thêm các view cần thiết
            buttonIncrease = itemView.findViewById(R.id.button_increase);
            buttonDecrease = itemView.findViewById(R.id.button_decrease);
            textProductNote = itemView.findViewById(R.id.text_product_note);
        }
    }


}
