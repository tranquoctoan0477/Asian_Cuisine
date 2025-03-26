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
import com.example.appasiancuisine.model.SpecialMenuModel;

import java.util.List;

public class SpecialMenuAdapter extends RecyclerView.Adapter<SpecialMenuAdapter.SpecialMenuViewHolder> {

    private final Context context;
    private final List<SpecialMenuModel> specialMenuList;

    public SpecialMenuAdapter(Context context, List<SpecialMenuModel> specialMenuList) {
        this.context = context;
        this.specialMenuList = specialMenuList;
    }

    @NonNull
    @Override
    public SpecialMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_special_menu, parent, false);
        return new SpecialMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialMenuViewHolder holder, int position) {
        SpecialMenuModel item = specialMenuList.get(position);
        holder.menuImage.setImageResource(item.getImageResId());
        holder.menuTitle.setText(item.getTitle());

        // Xóa các dòng món ăn cũ trước khi thêm mới
        holder.menuItems.removeAllViews();

        for (String food : item.getFoodItems()) {
            String[] parts = food.split(" - ");
            String name = parts[0];
            String price = (parts.length > 1) ? parts[1] : "";

            LinearLayout row = new LinearLayout(context);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            // Tên món
            TextView nameView = new TextView(context);
            nameView.setText("• " + name);
            nameView.setTextSize(14f);
            nameView.setTextColor(Color.WHITE);
            nameView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            // Giá món
            TextView priceView = new TextView(context);
            priceView.setText(price);
            priceView.setTextSize(14f);
            priceView.setTextColor(Color.LTGRAY);
            priceView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            // Padding cho từng dòng món
            int padV = (int) (4 * context.getResources().getDisplayMetrics().density);
            row.setPadding(0, padV, 0, padV);

            row.addView(nameView);
            row.addView(priceView);
            holder.menuItems.addView(row);
        }

        // ✅ Zig-zag layout (luân phiên vị trí ảnh)
        holder.rootLayout.removeAllViews();
        if (position % 2 == 0) {
            holder.rootLayout.addView(holder.menuImage);
            holder.rootLayout.addView(holder.menuContentLayout);
        } else {
            holder.rootLayout.addView(holder.menuContentLayout);
            holder.rootLayout.addView(holder.menuImage);
        }
    }

    @Override
    public int getItemCount() {
        return specialMenuList.size();
    }

    public static class SpecialMenuViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootLayout, menuContentLayout, menuItems;
        ImageView menuImage;
        TextView menuTitle;

        public SpecialMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            menuImage = itemView.findViewById(R.id.menuImage);
            menuTitle = itemView.findViewById(R.id.menuTitle);
            menuItems = itemView.findViewById(R.id.menuItemList);
            menuContentLayout = (LinearLayout) menuTitle.getParent();
        }
    }
}
