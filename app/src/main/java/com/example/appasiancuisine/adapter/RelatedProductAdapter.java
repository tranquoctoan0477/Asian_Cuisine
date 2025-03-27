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
import com.example.appasiancuisine.utils.AppConfig;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RelatedProductAdapter extends RecyclerView.Adapter<RelatedProductAdapter.RelatedProductViewHolder> {

    private final Context context;
    private final List<ProductDTO> relatedProducts;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ProductDTO product);
    }

    public RelatedProductAdapter(Context context, List<ProductDTO> relatedProducts, OnItemClickListener listener) {
        this.context = context;
        this.relatedProducts = relatedProducts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RelatedProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new RelatedProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedProductViewHolder holder, int position) {
        ProductDTO product = relatedProducts.get(position);

        String imageUrl = AppConfig.BASE_URL + product.getMainImg();
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.pd10)
                .error(R.drawable.pd10)
                .into(holder.foodImage);

        holder.foodName.setText(product.getName());
        holder.foodPrice.setText("$" + String.format("%.2f", product.getPrice()));
        holder.foodRating.setText("4.5"); // Rating mặc định

        holder.itemView.setOnClickListener(v -> {
            Log.d("RelatedProductAdapter", "Click: " + product.getName());
            listener.onItemClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return relatedProducts.size();
    }

    public static class RelatedProductViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage, foodStar;
        TextView foodName, foodPrice, foodRating;

        public RelatedProductViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodRating = itemView.findViewById(R.id.foodRating);
            foodStar = itemView.findViewById(R.id.foodStar);
        }
    }
}
