package com.example.appasiancuisine.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.CombinedMenuPagerAdapter;
import com.example.appasiancuisine.model.SpecialFoodModel;
import com.example.appasiancuisine.model.SpecialMenuModel;
import com.example.appasiancuisine.utils.SmoothFlipPageTransformer;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuFragment extends Fragment {

    private ViewPager2 menuViewPager;
    private CombinedMenuPagerAdapter combinedAdapter;
    private List<SpecialMenuModel> specialMenus;
    private List<SpecialFoodModel> allFoodItems;

    public MenuFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        menuViewPager = view.findViewById(R.id.menuViewPager);
        setupDummyData();

        List<List<SpecialFoodModel>> pagedFoodList = paginateFoodList(allFoodItems, 5);
        combinedAdapter = new CombinedMenuPagerAdapter(requireContext(), specialMenus, pagedFoodList);
        menuViewPager.setAdapter(combinedAdapter);
        menuViewPager.setPageTransformer(new SmoothFlipPageTransformer());

        return view;
    }

    private void setupDummyData() {
        specialMenus = new ArrayList<>();
        specialMenus.add(new SpecialMenuModel(
                R.drawable.bn03,
                "Sushi Đặc Biệt",
                Arrays.asList("Sashimi - $5.00", "Sushi cuộn - $4.50", "Trứng cá hồi - $6.00")
        ));
        specialMenus.add(new SpecialMenuModel(
                R.drawable.pd10,
                "Summer Sushi Set",
                Arrays.asList("Sashimi cá hồi - $6.00", "Sushi lươn - $5.50", "Sushi thanh cua - $4.80")
        ));

        allFoodItems = new ArrayList<>();
        allFoodItems.add(new SpecialFoodModel(R.drawable.pd10, "Sushi Cá hồi", "$5.50", "Ngon tuyệt"));
        allFoodItems.add(new SpecialFoodModel(R.drawable.pd10, "Sushi Tôm", "$5.00", "Tươi rói"));
        allFoodItems.add(new SpecialFoodModel(R.drawable.pd10, "Sashimi Bạch Tuộc", "$6.50", "Hải sản tươi"));
        allFoodItems.add(new SpecialFoodModel(R.drawable.pd10, "Maki Thanh Cua", "$4.50", "Cuộn ngon"));
        allFoodItems.add(new SpecialFoodModel(R.drawable.pd10, "Temaki Cá Ngừ", "$5.30", "Đậm đà"));
        allFoodItems.add(new SpecialFoodModel(R.drawable.pd10, "California Roll", "$5.90", "Phổ biến"));
    }

    private List<List<SpecialFoodModel>> paginateFoodList(List<SpecialFoodModel> fullList, int itemsPerPage) {
        List<List<SpecialFoodModel>> pages = new ArrayList<>();
        int total = fullList.size();
        for (int i = 0; i < total; i += itemsPerPage) {
            int end = Math.min(i + itemsPerPage, total);
            pages.add(new ArrayList<>(fullList.subList(i, end)));
        }
        return pages;
    }
}
