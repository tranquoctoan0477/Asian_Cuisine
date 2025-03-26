package com.example.appasiancuisine.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.data.dto.CategoryDTO;
import com.example.appasiancuisine.utils.AppConfig;  // Import AppConfig
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryDTO> categoryList;

    public CategoryAdapter(List<CategoryDTO> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryDTO category = categoryList.get(position);

        holder.title.setText(category.getName());

        // Lấy URL đầy đủ từ AppConfig
        String imageUrl = AppConfig.BASE_URL + category.getCategoryImg();
        Log.d("CategoryAdapter", "Loading image from URL: " + imageUrl);

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.pd10)
                .error(R.drawable.pd10)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.categoryImage);
            title = itemView.findViewById(R.id.categoryTitle);
        }
    }
}
