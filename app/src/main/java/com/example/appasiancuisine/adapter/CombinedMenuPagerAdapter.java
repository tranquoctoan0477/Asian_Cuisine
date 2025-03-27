package com.example.appasiancuisine.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.data.dto.CategoryDTO;
import com.example.appasiancuisine.data.dto.ProductDTO;
import com.example.appasiancuisine.view.ProductDetailActivity;

import java.util.List;

public class CombinedMenuPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MENU = 0;
    private static final int TYPE_PRODUCT = 1;

    private final Context context;
    private final List<CategoryDTO> categoryList;
    private final List<List<ProductDTO>> pagedProductList;
    private final List<ProductDTO> allProducts;

    public CombinedMenuPagerAdapter(Context context, List<CategoryDTO> categoryList, List<List<ProductDTO>> pagedProductList, List<ProductDTO> allProducts) {
        this.context = context;
        this.categoryList = categoryList;
        this.pagedProductList = pagedProductList;
        this.allProducts = allProducts;  // ✅ THÊM DÒNG NÀY
        Log.d("CombinedMenuPagerAdapter", "Khởi tạo với " + categoryList.size() + " danh mục và " + pagedProductList.size() + " trang sản phẩm.");
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? TYPE_MENU : TYPE_PRODUCT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TYPE_MENU) {
            View view = inflater.inflate(R.layout.layout_special_menu_page, parent, false);
            return new MenuViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.layout_page_food, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_MENU) {
            Log.d("CombinedMenuPagerAdapter", "Gán dữ liệu MENU tại vị trí: " + position);
            MenuViewHolder vh = (MenuViewHolder) holder;
            SpecialMenuAdapter adapter = new SpecialMenuAdapter(context, categoryList, allProducts);
            vh.recyclerView.setAdapter(adapter);
        } else {
            int pageIndex = position - 1;
            List<ProductDTO> productList = pagedProductList.get(pageIndex);
            Log.d("CombinedMenuPagerAdapter", "Gán dữ liệu PRODUCT tại trang: " + pageIndex + " (Sản phẩm: " + productList.size() + ")");

            ProductViewHolder vh = (ProductViewHolder) holder;

            // ⚠️ Truyền thêm sự kiện click có kiểm tra null
            SpecialFoodAdapter adapter = new SpecialFoodAdapter(context, productList, product -> {
                if (product != null) {
                    Log.d("CombinedMenuPagerAdapter", "Click vào sản phẩm: " + product.getName());

                    Intent intent = new Intent(context.getApplicationContext(), ProductDetailActivity.class);
                    intent.putExtra("productId", product.getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Cần thiết khi context không phải Activity
                    context.startActivity(intent);
                } else {
                    Log.w("CombinedMenuPagerAdapter", "Sản phẩm được click là null!");
                }
            });

            vh.recyclerView.setHasFixedSize(true); // ✅ Tối ưu hiệu suất nếu dữ liệu ổn định
            vh.recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return 1 + pagedProductList.size(); // 1 trang danh mục + các trang sản phẩm
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.menuPageRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.pageRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
