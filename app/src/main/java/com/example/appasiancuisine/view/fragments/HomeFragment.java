package com.example.appasiancuisine.view.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.BannerAdapter;
import com.example.appasiancuisine.adapter.CategoryAdapter;
import com.example.appasiancuisine.adapter.FoodAdapter;
import com.example.appasiancuisine.data.dto.CategoryDTO;
import com.example.appasiancuisine.data.dto.HomeResponse;
import com.example.appasiancuisine.data.dto.ProductDTO;
import com.example.appasiancuisine.presenter.HomeContract;
import com.example.appasiancuisine.presenter.HomePresenter;
import com.example.appasiancuisine.presenter.ReloadableFragment;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements HomeContract.View, ReloadableFragment {

    private ViewPager2 bannerViewPager;
    private WormDotsIndicator bannerDots;
    private RecyclerView categoryRecyclerView, foodRecyclerView;
    private ImageView foodFilterIcon, searchIcon;
    private EditText searchInput;

    private CategoryAdapter categoryAdapter;
    private FoodAdapter foodAdapter;

    private List<CategoryDTO> categoryList = new ArrayList<>();
    private List<ProductDTO> fullProductList = new ArrayList<>();
    private List<ProductDTO> originalProductList = new ArrayList<>();

    private HomePresenter homePresenter;

    private int currentPage = 0;  // Biến theo dõi trang hiện tại
    private final int limit = 6;   // Số lượng sản phẩm mỗi lần tải
    private String currentSearchQuery = ""; // Lưu lại truy vấn tìm kiếm hiện tại
    private ProgressBar loadingSpinner;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homePresenter = new HomePresenter(this);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);

        setupBanner(view);
        setupCategorySection(view);
        setupFoodSection(view);  // Đây là nơi bạn khởi tạo foodRecyclerView

        setupSearch(view);   // gọi hàm tìm kiếm
        setupFilter(view);   //gọi hàm lọc

        // Thêm sự kiện cuộn
        foodRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) { // Cuộn đến cuối
                    currentPage++;
                    Log.d("HomeFragment", "Cuộn đến cuối, tải trang: " + currentPage);

                    // Gọi API để tải thêm dữ liệu, chỉ tải dữ liệu khi có sản phẩm mới
                    homePresenter.fetchHomeData(currentSearchQuery, currentPage, limit);
                }
            }
        });

        // Đảm bảo gọi API ban đầu để tải dữ liệu
        homePresenter.fetchHomeData("", 0, limit);

        return view;
    }


    private void setupBanner(View view) {
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerDots = view.findViewById(R.id.bannerDots);

        List<Integer> bannerImages = Arrays.asList(
                R.drawable.bn03, R.drawable.bn04, R.drawable.bn05, R.drawable.bn06
        );

        BannerAdapter bannerAdapter = new BannerAdapter(requireContext(), bannerImages);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerDots.setViewPager2(bannerViewPager);
    }

    private void setupCategorySection(View view) {
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            // Khi người dùng click vào danh mục bất kỳ
            filterProductsByCategory(category.getId());
        });
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    // Lọc sản phẩm theo danh mục được chọn và hiển thị lên giao diện
    private void filterProductsByCategory(int categoryId) {
        List<ProductDTO> filteredList = new ArrayList<>();
        for (ProductDTO product : originalProductList) {
            if (product.getCategoryId() == categoryId) {
                filteredList.add(product);
            }
        }

        fullProductList.clear();
        fullProductList.addAll(filteredList);
        foodAdapter.updateData(fullProductList);
        foodRecyclerView.scrollToPosition(0);
    }


    private void setupFoodSection(View view) {
        foodRecyclerView = view.findViewById(R.id.foodRecyclerView);
        foodRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        foodAdapter = new FoodAdapter(requireContext(), fullProductList, product -> {
            // TODO: Chuyển sang ProductDetailActivity
        });
        foodRecyclerView.setAdapter(foodAdapter);
    }

    private void setupSearch(View view) {
        searchInput = view.findViewById(R.id.edt_search);
        searchIcon = view.findViewById(R.id.ic_search);
        searchIcon.setOnClickListener(v -> {
            String keyword = searchInput.getText().toString().trim();
            currentSearchQuery = keyword; // Lưu lại từ khóa tìm kiếm

            // Xóa các sản phẩm cũ trước khi gọi API với từ khóa mới
            fullProductList.clear();
            foodAdapter.updateData(fullProductList); // Cập nhật lại adapter để làm mới danh sách hiển thị

            Log.d("HomeFragment", "Gọi API tìm kiếm với từ khóa: " + keyword);
            homePresenter.fetchHomeData(keyword, 0, limit); // Gọi lại API với từ khóa tìm kiếm

            foodRecyclerView.scrollToPosition(0);
        });
    }

    private void setupFilter(View view) {
        foodFilterIcon = view.findViewById(R.id.foodFilterIcon);
        foodFilterIcon.setOnClickListener(anchorView -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), anchorView);
            popupMenu.getMenuInflater().inflate(R.menu.menu_food_filter, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                List<ProductDTO> filteredList = new ArrayList<>();

                if (id == R.id.menu_product_new) {
                    for (ProductDTO product : originalProductList) {
                        if (Math.random() >= 0.8) {
                            filteredList.add(product);
                        }
                    }
                    Log.d("HomeFragment", "Lọc sản phẩm mới: " + filteredList.size());

                } else if (id == R.id.menu_filter_discount) {
                    for (ProductDTO product : originalProductList) {
                        if (product.getVoucherCode() != null) {
                            filteredList.add(product);
                        }
                    }
                    Log.d("HomeFragment", "Lọc sản phẩm giảm giá: " + filteredList.size());
                }

                // Luôn đảm bảo clear fullProductList trước khi thêm mới
                fullProductList.clear();
                fullProductList.addAll(filteredList);

                foodAdapter.updateData(fullProductList);
                foodRecyclerView.scrollToPosition(0);

                return true;
            });


            popupMenu.show();
        });
    }

    // ==========================
    // Xử lý dữ liệu từ Presenter
    // ==========================

    @Override
    public void onHomeDataLoaded(HomeResponse response) {
        // Ẩn spinner khi có dữ liệu
        loadingSpinner.setVisibility(View.GONE);

        // Cập nhật danh mục
        categoryList.clear();
        categoryList.addAll(response.getCategories());
        categoryAdapter.notifyDataSetChanged();

        // Kiểm tra và log số lượng danh mục
        Log.d("HomeFragment", "Danh mục: " + response.getCategories().size() + " danh mục được tải");

        // Nếu tải lại trang đầu tiên, clear cả originalProductList và fullProductList
        if (currentPage == 0) {
            originalProductList.clear();
            fullProductList.clear();
        }

        // Thêm sản phẩm mới vào cả hai danh sách
        originalProductList.addAll(response.getAllProducts());
        fullProductList.addAll(response.getAllProducts());

        // Log số lượng sản phẩm hiện tại
        Log.d("HomeFragment", "Sản phẩm hiện tại: " + fullProductList.size() + " sản phẩm");

        // Log chi tiết các sản phẩm
        for (ProductDTO product : fullProductList) {
            Log.d("HomeFragment", "Sản phẩm: " + product.getName() + ", Giá: " + product.getPrice());
        }

        // Cập nhật dữ liệu cho adapter
        foodAdapter.updateData(fullProductList);

        // Scroll về đầu danh sách sau khi cập nhật dữ liệu
        foodRecyclerView.scrollToPosition(0);
    }




    @Override
    public void onHomeDataError(String errorMessage) {
        loadingSpinner.setVisibility(View.GONE); // ẩn spinner khi có lỗi
        Toast.makeText(requireContext(), "Lỗi tải dữ liệu: " + errorMessage, Toast.LENGTH_LONG).show();
        Log.e("HomeFragment", "Lỗi tải dữ liệu: " + errorMessage);
    }

    @Override
    public void reload() {
        currentPage = 0;
        currentSearchQuery = ""; // Thêm dòng này để reset từ khóa tìm kiếm
        fullProductList.clear();
        originalProductList.clear(); // Thêm dòng này nếu bạn đã thêm originalProductList
        foodAdapter.notifyDataSetChanged();

        // Hiển thị spinner loading
        loadingSpinner.setVisibility(View.VISIBLE);

        homePresenter.fetchHomeData("", currentPage, limit);

        // Tự động ẩn spinner sau 2–3 giây
        new Handler().postDelayed(() -> {
            loadingSpinner.setVisibility(View.GONE);
        }, 2500);
    }

    public void setupDataFromApi(HomeResponse response) {
        // Gửi dữ liệu ban đầu giống như reload nhưng không hiển thị loading spinner
        currentPage = 0;
        currentSearchQuery = "";
        fullProductList.clear();
        originalProductList.clear();

        categoryList.clear();
        categoryList.addAll(response.getCategories());
        categoryAdapter.notifyDataSetChanged();

        originalProductList.addAll(response.getAllProducts());
        fullProductList.addAll(response.getAllProducts());
        foodAdapter.updateData(fullProductList);

        foodRecyclerView.scrollToPosition(0);
    }


}
