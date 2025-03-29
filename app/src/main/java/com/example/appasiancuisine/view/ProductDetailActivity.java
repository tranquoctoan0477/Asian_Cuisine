package com.example.appasiancuisine.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.snackbar.Snackbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.ProductImageSliderAdapter;
import com.example.appasiancuisine.adapter.RelatedProductAdapter;
import com.example.appasiancuisine.data.dto.ProductDetailDTO;
import com.example.appasiancuisine.presenter.ProductDetailContract;
import com.example.appasiancuisine.presenter.ProductDetailPresenter;
import com.example.appasiancuisine.utils.AppConfig;
import com.example.appasiancuisine.utils.PreferenceManager;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity implements ProductDetailContract.View {

    private ViewPager2 viewPagerImages;
    private WormDotsIndicator dotsIndicator;
    private RecyclerView rvRelatedProducts;
    private TextView tvProductName, tvProductDescription, tvPrice;
    private Button btnAddToCart, btnOrder;
    private ProductDetailPresenter presenter;
    private int productId;
    private ProductDetailDTO currentProductDetail; // Biến lưu trữ sản phẩm hiện tại


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();

        productId = getIntent().getIntExtra("productId", -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        presenter = new ProductDetailPresenter(this);
        presenter.fetchProductDetail(productId);

        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void initViews() {
        viewPagerImages = findViewById(R.id.viewPagerImages);
        dotsIndicator = findViewById(R.id.wormDotsIndicator);
        rvRelatedProducts = findViewById(R.id.rvRelatedProducts);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvPrice = findViewById(R.id.tvPrice);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnOrder = findViewById(R.id.btnOrder);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onProductDetailLoaded(ProductDetailDTO productDetail) {
        if (productDetail == null || productDetail.getProduct() == null) {
            Toast.makeText(this, "Không có dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentProductDetail = productDetail; // Lưu sản phẩm hiện tại

        tvProductName.setText(productDetail.getProduct().getName());
        tvProductDescription.setText(productDetail.getProduct().getDescription());
        tvPrice.setText(String.format("%,d₫", (int) productDetail.getProduct().getPrice()));

        // Hiển thị hình ảnh sản phẩm
        ProductImageSliderAdapter imageAdapter = new ProductImageSliderAdapter(this, productDetail.getImages());
        viewPagerImages.setAdapter(imageAdapter);
        dotsIndicator.setViewPager2(viewPagerImages);

        // ✅ Xử lý hiển thị danh sách sản phẩm liên quan
        if (productDetail.getRelatedProducts() != null && !productDetail.getRelatedProducts().isEmpty()) {
            RelatedProductAdapter relatedAdapter = new RelatedProductAdapter(
                    this,
                    productDetail.getRelatedProducts(),
                    product -> {
                        if (product != null) {
                            Intent intent = new Intent(ProductDetailActivity.this, ProductDetailActivity.class);
                            intent.putExtra("productId", product.getId());
                            startActivity(intent);
                        }
                    }
            );

            rvRelatedProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvRelatedProducts.setAdapter(relatedAdapter);

            Log.d("ProductDetailActivity", "Related Products loaded: " + productDetail.getRelatedProducts().size());
        } else {
            Log.d("ProductDetailActivity", "Không có sản phẩm liên quan.");
        }
    }


    @Override
    public void onProductDetailError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void addToCart() {
        if (currentProductDetail == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = 1;
        String url = AppConfig.CART_ADD_URL;
        String productName = currentProductDetail.getProduct().getName();
        String imageUrl = getCurrentImageUrl(); // 🔥 Lấy URL ảnh đầy đủ

        Log.d("ProductDetailActivity", "🌟 URL ảnh đầy đủ đang gửi: " + imageUrl);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId", productId);
            jsonObject.put("quantity", quantity);
            jsonObject.put("productName", productName);
            jsonObject.put("imageUrl", imageUrl); // 🔥 Thêm URL ảnh đầy đủ vào JSON
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> showAddToCartSnackbar(),
                error -> {
                    Toast.makeText(this, "Lỗi khi thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    Log.e("AddToCart", "Lỗi: " + error.getMessage());
                }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                PreferenceManager preferenceManager = new PreferenceManager(ProductDetailActivity.this);
                String accessToken = preferenceManager.getAccessToken();

                if (accessToken == null || accessToken.isEmpty()) {
                    Toast.makeText(ProductDetailActivity.this, "Bạn cần đăng nhập trước khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    return headers;
                }

                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public String getCurrentImageUrl() {
        if (currentProductDetail != null &&
                currentProductDetail.getImages() != null &&
                !currentProductDetail.getImages().isEmpty()) {
            String imageUrl = AppConfig.BASE_URL + currentProductDetail.getImages().get(0); // 🔥 Trả về URL đầy đủ
            Log.d("ProductDetailActivity", "🌟 URL ảnh đang dùng: " + imageUrl);
            return imageUrl;
        }
        return ""; // Trả về chuỗi rỗng nếu không có ảnh
    }

    private void showAddToCartSnackbar() {
        Snackbar.make(findViewById(android.R.id.content), "Đã thêm vào giỏ hàng", Snackbar.LENGTH_LONG)
                .setAction("Xem giỏ hàng", v -> goToCartActivity())
                .show();
    }

    private void goToCartActivity() {
        Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
        startActivity(intent);
    }

}
