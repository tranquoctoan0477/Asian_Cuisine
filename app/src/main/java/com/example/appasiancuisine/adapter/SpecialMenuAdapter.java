package com.example.appasiancuisine.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.data.dto.CategoryDTO;
import com.example.appasiancuisine.data.dto.ProductDTO;
import com.example.appasiancuisine.utils.AppConfig;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SpecialMenuAdapter extends RecyclerView.Adapter<SpecialMenuAdapter.MenuItemViewHolder> {

    private final Context context;
    private final List<CategoryDTO> categoryList;
    private final List<ProductDTO> allProducts;

    public SpecialMenuAdapter(Context context, List<CategoryDTO> categoryList, List<ProductDTO> allProducts) {
        this.context = context;
        this.categoryList = categoryList;
        this.allProducts = allProducts;
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_special_menu, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        CategoryDTO category = categoryList.get(position);
        holder.title.setText(category.getName());

        // Load ảnh danh mục
        Picasso.get()
                .load(AppConfig.BASE_URL + category.getCategoryImg())
                .placeholder(R.drawable.pd10)
                .error(R.drawable.pd10)
                .into(holder.image);

        // Xóa view cũ trong danh sách món
        holder.menuItemList.removeAllViews();

        // Lọc sản phẩm thuộc danh mục
        List<ProductDTO> categoryProducts = new ArrayList<>();
        for (ProductDTO product : allProducts) {
            if (product.getCategoryId() == category.getId()) {
                categoryProducts.add(product);
            }
        }

        // Hiển thị tối đa 2 món đầu tiên
        for (int i = 0; i < Math.min(2, categoryProducts.size()); i++) {
            ProductDTO product = categoryProducts.get(i);

            TextView itemView = new TextView(context);
            itemView.setText(product.getName() + " - $" + String.format("%.2f", product.getPrice()));
            itemView.setTextColor(Color.WHITE);
            itemView.setTextSize(14);
            itemView.setPadding(0, 4, 0, 4);

            holder.menuItemList.addView(itemView);
        }

        // Zig-zag: ảnh trái phải xen kẽ
        if (position % 2 == 0) {
            holder.rootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        } else {
            holder.rootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        LinearLayout menuItemList;
        LinearLayout rootLayout;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.menuImage);
            title = itemView.findViewById(R.id.menuTitle);
            menuItemList = itemView.findViewById(R.id.menuItemList);   // ✅ fix lỗi
            rootLayout = itemView.findViewById(R.id.rootLayout);       // ✅ fix lỗi
        }
    }
}
