package com.example.appasiancuisine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.model.SpecialFoodModel;

import java.util.List;

public class FoodPagerAdapter extends RecyclerView.Adapter<FoodPagerAdapter.PageViewHolder> {

    private final Context context;
    private final List<List<SpecialFoodModel>> pagedFoodList;

    public FoodPagerAdapter(Context context, List<List<SpecialFoodModel>> pagedFoodList) {
        this.context = context;
        this.pagedFoodList = pagedFoodList;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_page_food, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        List<SpecialFoodModel> foodsInPage = pagedFoodList.get(position);

        SpecialFoodAdapter adapter = new SpecialFoodAdapter(context, foodsInPage);
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return pagedFoodList.size(); // số lượng trang
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.pageRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
