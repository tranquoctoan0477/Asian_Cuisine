package com.example.appasiancuisine.presenter;

import com.example.appasiancuisine.data.dto.ProductDetailDTO;

public interface ProductDetailContract {

    interface View {
        void onProductDetailLoaded(ProductDetailDTO productDetail);
        void onProductDetailError(String message);
    }

    interface Presenter {
        void fetchProductDetail(int productId);
    }
}
