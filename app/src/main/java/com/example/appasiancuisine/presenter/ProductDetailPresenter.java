package com.example.appasiancuisine.presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.appasiancuisine.data.dto.ProductDetailDTO;
import com.example.appasiancuisine.presenter.ProductDetailContract;
import com.example.appasiancuisine.utils.AppConfig;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductDetailPresenter implements ProductDetailContract.Presenter {

    private final ProductDetailContract.View view;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ProductDetailPresenter(ProductDetailContract.View view) {
        this.view = view;
    }

    @Override
    public void fetchProductDetail(int productId) {
        executor.execute(() -> {
            try {
                String apiUrl = AppConfig.ProductDetail_URL + "/" + productId;
                Log.d("ProductDetailPresenter", "Gọi API: " + apiUrl);

                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    Gson gson = new Gson();
                    ProductDetailDTO productDetail = gson.fromJson(response.toString(), ProductDetailDTO.class);

                    handler.post(() -> view.onProductDetailLoaded(productDetail));
                } else {
                    handler.post(() -> view.onProductDetailError("Lỗi kết nối: " + responseCode));
                }

            } catch (Exception e) {
                Log.e("ProductDetailPresenter", "Lỗi: ", e);
                handler.post(() -> view.onProductDetailError("Lỗi: " + e.getMessage()));
            }
        });
    }
}
