package com.example.appasiancuisine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.data.dto.ProductDTO;
import com.example.appasiancuisine.utils.AppConfig;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SpecialFoodAdapter extends RecyclerView.Adapter<SpecialFoodAdapter.SpecialFoodViewHolder> {



    private final Context context;
    private final List<ProductDTO> foodList;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ProductDTO product);
    }


    public SpecialFoodAdapter(Context context, List<ProductDTO> foodList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.foodList = foodList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SpecialFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_special_food, parent, false);
        return new SpecialFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialFoodViewHolder holder, int position) {
        ProductDTO food = foodList.get(position);

        // Load ảnh từ API với Picasso
        String fullImageUrl = AppConfig.BASE_URL + food.getMainImg();
        Picasso.get()
                .load(fullImageUrl)
                .placeholder(R.drawable.pd10)
                .error(R.drawable.pd10)
                .into(holder.foodImage);

        holder.foodName.setText(food.getName());
        holder.foodPrice.setText(String.format("$%.2f", food.getPrice()));
        holder.foodDesc.setText(food.getDescription());

        // Hiệu ứng zig-zag
        if (position % 2 == 0) {
            holder.rootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR); // Ảnh trái
        } else {
            holder.rootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); // Ảnh phải
        }

        // Thêm click listener nếu cần
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class SpecialFoodViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootLayout;
        ImageView foodImage;
        TextView foodName, foodPrice, foodDesc;

        public SpecialFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            foodImage = itemView.findViewById(R.id.specialFoodImage);
            foodName = itemView.findViewById(R.id.specialFoodName);
            foodPrice = itemView.findViewById(R.id.specialFoodPrice);
            foodDesc = itemView.findViewById(R.id.specialFoodDesc);
        }
    }
}
