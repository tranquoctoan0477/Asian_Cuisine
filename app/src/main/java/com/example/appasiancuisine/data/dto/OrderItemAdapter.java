package com.example.appasiancuisine.data.dto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.data.dto.OrderItemDTO;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private Context context;
    private List<OrderItemDTO> orderItemList;

    public OrderItemAdapter(Context context, List<OrderItemDTO> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItemDTO item = orderItemList.get(position);

        holder.textProductName.setText(item.getProductName());
        holder.textProductPrice.setText(item.getPrice() + "₫");
        holder.textQuantity.setText("Số lượng: " + item.getQuantity());

        // Nếu có ghi chú, hiển thị; nếu không, ẩn đi
        if (item.getNote() != null && !item.getNote().isEmpty()) {
            holder.textProductNote.setText("Ghi chú: " + item.getNote());
            holder.textProductNote.setVisibility(View.VISIBLE);
        } else {
            holder.textProductNote.setVisibility(View.GONE);
        }

        // Load ảnh bằng Picasso
        Picasso.get()
                .load(item.getMainImg())   // Đường dẫn ảnh từ API
                .placeholder(R.drawable.logo)  // Ảnh tạm khi đang tải
                .error(R.drawable.img_logo)        // Ảnh lỗi nếu tải thất bại
                .into(holder.imageProduct);
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName, textProductPrice, textQuantity, textProductNote;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            textProductNote = itemView.findViewById(R.id.text_product_note);
        }
    }
}

