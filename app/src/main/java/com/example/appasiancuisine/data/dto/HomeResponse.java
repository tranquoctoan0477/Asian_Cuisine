package com.example.appasiancuisine.data.dto;

import java.util.List;

public class HomeResponse {
    private List<CategoryDTO> categories;
    private List<ProductDTO> allProducts;
    private List<ProductDTO> newProducts;
    private List<ProductDTO> promoProducts;

    public HomeResponse() {
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public List<ProductDTO> getAllProducts() {
        return allProducts;
    }

    public void setAllProducts(List<ProductDTO> allProducts) {
        this.allProducts = allProducts;
    }

    public List<ProductDTO> getNewProducts() {
        return newProducts;
    }

    public void setNewProducts(List<ProductDTO> newProducts) {
        this.newProducts = newProducts;
    }

    public List<ProductDTO> getPromoProducts() {
        return promoProducts;
    }

    public void setPromoProducts(List<ProductDTO> promoProducts) {
        this.promoProducts = promoProducts;
    }
}
