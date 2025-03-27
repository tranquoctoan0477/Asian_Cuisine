package com.example.appasiancuisine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.data.dto.ProductDTO;

import java.util.List;

public class FoodPagerAdapter extends RecyclerView.Adapter<FoodPagerAdapter.PageViewHolder> {

    private final Context context;
    private final List<List<ProductDTO>> pagedProductList;

    public FoodPagerAdapter(Context context, List<List<ProductDTO>> pagedProductList) {
        this.context = context;
        this.pagedProductList = pagedProductList;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_page_food, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        List<ProductDTO> foodsInPage = pagedProductList.get(position); // sử dụng ProductDTO ở đây
        SpecialFoodAdapter adapter = new SpecialFoodAdapter(context, foodsInPage);
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return pagedProductList.size();
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.pageRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
