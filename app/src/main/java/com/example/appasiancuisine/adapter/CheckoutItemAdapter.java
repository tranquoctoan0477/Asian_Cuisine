package com.example.appasiancuisine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.data.dto.CheckoutItemDTO;
import com.example.appasiancuisine.utils.AppConfig;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.CheckoutViewHolder> {

    private final Context context;
    private final List<CheckoutItemDTO> itemList;

    public CheckoutItemAdapter(Context context, List<CheckoutItemDTO> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkout_product, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CheckoutItemDTO item = itemList.get(position);

        // 👉 Log để kiểm tra dữ liệu
        System.out.println("📝 Product: " + item.getProductName() + " - Note: " + item.getNote());

        holder.textProductName.setText(item.getProductName());

        if (item.getNote() != null && !item.getNote().isEmpty()) {
            holder.textProductNote.setVisibility(View.VISIBLE);
            holder.textProductNote.setText(item.getNote());
        } else {
            holder.textProductNote.setVisibility(View.GONE);
        }

        holder.textQuantity.setText("x" + item.getQuantity());
        double subtotal = item.getPrice() * item.getQuantity();
        holder.textProductPrice.setText(String.format("%,d₫", (int) subtotal));

        if (item.getThumbnail() != null && !item.getThumbnail().isEmpty()) {
            String fullUrl = item.getThumbnail().startsWith("http")
                    ? item.getThumbnail()
                    : AppConfig.BASE_URL + item.getThumbnail();

            Picasso.get()
                    .load(fullUrl)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.img_logo)
                    .into(holder.imageProduct);
        } else {
            holder.imageProduct.setImageResource(R.drawable.img_logo);
        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProduct;
        TextView textProductName, textProductNote, textProductPrice, textQuantity;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductNote = itemView.findViewById(R.id.text_product_note);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            textQuantity = itemView.findViewById(R.id.text_quantity);
        }
    }
}
