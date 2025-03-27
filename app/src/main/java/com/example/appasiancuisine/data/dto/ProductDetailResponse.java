package com.example.appasiancuisine.data.dto;

import java.util.List;

public class ProductDetailResponse {

    private ProductDTO product;
    private List<String> images;
    private List<ProductDTO> relatedProducts;

    public ProductDetailResponse() {}

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<ProductDTO> getRelatedProducts() {
        return relatedProducts;
    }

    public void setRelatedProducts(List<ProductDTO> relatedProducts) {
        this.relatedProducts = relatedProducts;
    }
}
