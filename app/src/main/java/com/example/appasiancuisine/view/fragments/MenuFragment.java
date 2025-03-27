package com.example.appasiancuisine.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.CombinedMenuPagerAdapter;
import com.example.appasiancuisine.data.dto.CategoryDTO;
import com.example.appasiancuisine.data.dto.HomeResponse;
import com.example.appasiancuisine.data.dto.ProductDTO;
import com.example.appasiancuisine.utils.SmoothFlipPageTransformer;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private ViewPager2 menuViewPager;
    private CombinedMenuPagerAdapter combinedAdapter;
    private final List<CategoryDTO> categories = new ArrayList<>();
    private final List<ProductDTO> allProducts = new ArrayList<>();

    public MenuFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        menuViewPager = view.findViewById(R.id.menuViewPager);
        menuViewPager.setPageTransformer(new SmoothFlipPageTransformer());

        return view;
    }

    // Hàm này được gọi từ bên ngoài khi có dữ liệu API trả về
    public void setupDataFromApi(HomeResponse response) {
        Log.d("MenuFragment", "Nhận dữ liệu API thành công.");

        categories.clear();
        categories.addAll(response.getCategories());
        Log.d("MenuFragment", "Số lượng danh mục: " + categories.size());

        allProducts.clear();
        allProducts.addAll(response.getAllProducts());
        Log.d("MenuFragment", "Số lượng sản phẩm: " + allProducts.size());

        setupViewPager();
    }

    private void setupViewPager() {
        List<List<ProductDTO>> pagedProductList = paginateProductList(allProducts, 5);
        Log.d("MenuFragment", "Số lượng trang sản phẩm phân ra: " + pagedProductList.size());

        combinedAdapter = new CombinedMenuPagerAdapter(requireContext(), categories, pagedProductList, allProducts);
        menuViewPager.setAdapter(combinedAdapter);

        Log.d("MenuFragment", "Đã thiết lập xong Adapter và Transformer");
    }

    private List<List<ProductDTO>> paginateProductList(List<ProductDTO> fullList, int itemsPerPage) {
        List<List<ProductDTO>> pages = new ArrayList<>();
        int total = fullList.size();
        for (int i = 0; i < total; i += itemsPerPage) {
            int end = Math.min(i + itemsPerPage, total);
            pages.add(new ArrayList<>(fullList.subList(i, end)));
        }
        return pages;
    }
}
