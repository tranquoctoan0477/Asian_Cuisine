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
import com.example.appasiancuisine.data.dto.OrderItemDTO;
import com.example.appasiancuisine.utils.AppConfig;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private final Context context;
    private final List<OrderItemDTO> orderItemList;

    public OrderDetailAdapter(Context context, List<OrderItemDTO> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderItemDTO orderItem = orderItemList.get(position);

        // Hiển thị tên sản phẩm
        holder.textProductName.setText(orderItem.getProductName());

        // Hiển thị số lượng
        holder.textProductQuantity.setText("Số lượng: " + orderItem.getQuantity());

        // Hiển thị giá sản phẩm
        String formattedPrice = NumberFormat.getInstance(Locale.US).format(orderItem.getPrice()) + "₫";
        holder.textProductPrice.setText(formattedPrice);

        // Hiển thị ghi chú nếu có
        if (orderItem.getNote() != null && !orderItem.getNote().isEmpty()) {
            holder.textProductNote.setText("Ghi chú: " + orderItem.getNote());
            holder.textProductNote.setVisibility(View.VISIBLE);
        } else {
            holder.textProductNote.setVisibility(View.GONE);
        }

        // Load ảnh sản phẩm (Sử dụng Picasso)
        String mainImg = orderItem.getMainImg();
        String imageUrl;

        // Kiểm tra URL ảnh
        if (mainImg.startsWith("http")) { // URL đã đầy đủ
            imageUrl = mainImg;
        } else { // URL tương đối, cần ghép thêm BASE_URL
            imageUrl = AppConfig.BASE_URL + mainImg;
        }

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.pd10) // Ảnh hiển thị khi đang tải
                .error(R.drawable.pd10) // Ảnh hiển thị khi lỗi
                .into(holder.imageProduct);
    }


    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName, textProductQuantity, textProductPrice, textProductNote;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductQuantity = itemView.findViewById(R.id.text_product_quantity);
            textProductPrice = itemView.findViewById(R.id.text_product_price);
            textProductNote = itemView.findViewById(R.id.text_product_note);
        }
    }
}
