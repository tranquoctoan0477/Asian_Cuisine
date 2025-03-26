package com.example.appasiancuisine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.model.SpecialFoodModel;
import com.example.appasiancuisine.model.SpecialMenuModel;

import java.util.List;

public class CombinedMenuPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MENU = 0;
    private static final int TYPE_FOOD = 1;

    private final Context context;
    private final List<SpecialMenuModel> specialMenuList;
    private final List<List<SpecialFoodModel>> pagedFoodList;

    public CombinedMenuPagerAdapter(Context context, List<SpecialMenuModel> specialMenuList, List<List<SpecialFoodModel>> pagedFoodList) {
        this.context = context;
        this.specialMenuList = specialMenuList;
        this.pagedFoodList = pagedFoodList;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? TYPE_MENU : TYPE_FOOD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TYPE_MENU) {
            View view = inflater.inflate(R.layout.layout_special_menu_page, parent, false); // bạn sẽ tạo file này ở Bước 2
            return new MenuViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.layout_page_food, parent, false);
            return new FoodViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_MENU) {
            MenuViewHolder vh = (MenuViewHolder) holder;
            SpecialMenuAdapter adapter = new SpecialMenuAdapter(context, specialMenuList);
            vh.recyclerView.setAdapter(adapter);
        } else {
            FoodViewHolder vh = (FoodViewHolder) holder;
            List<SpecialFoodModel> foodList = pagedFoodList.get(position - 1); // vì vị trí 0 là menu
            SpecialFoodAdapter adapter = new SpecialFoodAdapter(context, foodList);
            vh.recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return 1 + pagedFoodList.size(); // 1 trang menu + N trang food
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.menuPageRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.pageRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
