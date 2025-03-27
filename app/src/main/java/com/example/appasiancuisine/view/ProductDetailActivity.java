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

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.ProductImageSliderAdapter;
import com.example.appasiancuisine.adapter.RelatedProductAdapter;
import com.example.appasiancuisine.data.dto.ProductDetailDTO;
import com.example.appasiancuisine.data.dto.ProductDTO;
import com.example.appasiancuisine.presenter.ProductDetailContract;
import com.example.appasiancuisine.presenter.ProductDetailPresenter;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity implements ProductDetailContract.View {

    private ViewPager2 viewPagerImages;
    private WormDotsIndicator dotsIndicator;
    private RecyclerView rvRelatedProducts;
    private TextView tvProductName, tvProductDescription, tvPrice;
    private Button btnAddToCart, btnOrder;
    private ProductDetailPresenter presenter;
    private int productId;

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

        Log.d("ProductDetailActivity", "Dữ liệu sản phẩm nhận được: " + productDetail.getProduct().getName());

        // Hiển thị thông tin sản phẩm
        tvProductName.setText(productDetail.getProduct().getName());
        tvProductDescription.setText(productDetail.getProduct().getDescription());
        tvPrice.setText(String.format("$%.2f", productDetail.getProduct().getPrice()));

        // Slider hình ảnh
        ProductImageSliderAdapter imageAdapter = new ProductImageSliderAdapter(this, productDetail.getImages());
        viewPagerImages.setAdapter(imageAdapter);
        dotsIndicator.setViewPager2(viewPagerImages);

        // Related Products
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
    }

    @Override
    public void onProductDetailError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
        finish();
    }
}
