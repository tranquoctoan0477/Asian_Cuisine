package com.example.appasiancuisine.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.data.dto.ProductDTO;
import com.example.appasiancuisine.utils.AppConfig;  // Import AppConfig
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<ProductDTO> productList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ProductDTO product);
    }

    public FoodAdapter(Context context, List<ProductDTO> productList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.productList = productList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        ProductDTO product = productList.get(position);

        // Lấy URL đầy đủ từ AppConfig
        String imageUrl = AppConfig.BASE_URL + product.getMainImg();
        Log.d("FoodAdapter", "Loading image from URL: " + imageUrl);

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.pd10)
                .error(R.drawable.pd10)
                .into(holder.foodImage);

        holder.foodName.setText(product.getName());
        Log.d("FoodAdapter", "Food Name: " + product.getName());

        holder.foodPrice.setText("$" + String.format("%.2f", product.getPrice()));
        Log.d("FoodAdapter", "Food Price: $" + String.format("%.2f", product.getPrice()));

        holder.foodRating.setText("4.5"); // Local rating
        Log.d("FoodAdapter", "Food Rating: 4.5");

        holder.itemView.setOnClickListener(v -> {
            Log.d("FoodAdapter", "Item clicked: " + product.getName());
            onItemClickListener.onItemClick(product);
        });
    }

    @Override
    public int getItemCount() {
        Log.d("FoodAdapter", "Item count: " + productList.size());
        return productList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage, foodStar;
        TextView foodName, foodPrice, foodRating;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodRating = itemView.findViewById(R.id.foodRating);
            foodStar = itemView.findViewById(R.id.foodStar);
        }
    }

    public void updateData(List<ProductDTO> newList) {
        Log.d("FoodAdapter", "Cập nhật dữ liệu mới: " + newList.size() + " sản phẩm");
        this.productList = newList;
        notifyDataSetChanged();
    }

}
