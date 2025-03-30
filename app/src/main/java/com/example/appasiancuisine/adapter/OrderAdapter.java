package com.example.appasiancuisine.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.data.dto.OrderDTO;
import com.example.appasiancuisine.view.OrderDetailActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final Context context;
    private final List<OrderDTO> orderList;

    public OrderAdapter(Context context, List<OrderDTO> orderList) {
        this.context = context;
        this.orderList = orderList;

        // Log danh sách đơn hàng nhận được
        if (orderList == null || orderList.isEmpty()) {
            Log.w("ORDER_ADAPTER", "⚠️ Danh sách đơn hàng RỖNG hoặc NULL!");
        } else {
            Log.d("ORDER_ADAPTER", "✅ Nhận được " + orderList.size() + " đơn hàng để hiển thị.");
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        if (orderList == null || orderList.isEmpty()) {
            Log.e("ORDER_ADAPTER", "❌ orderList rỗng! Không thể bind dữ liệu.");
            return;
        }

        OrderDTO order = orderList.get(position);
        Log.d("ORDER_ADAPTER", "📦 Binding đơn hàng #" + order.getId());

        holder.textOrderId.setText("#" + order.getId());
        holder.textOrderStatus.setText(order.getStatus());

        // Format số tiền
        String formattedPrice = NumberFormat.getInstance(Locale.US).format(order.getTotal()) + "₫";
        holder.textOrderTotal.setText(formattedPrice);

        // Hiển thị ngày tạo (vì hiện tại đã chuyển sang String)
        if (order.getCreatedAt() != null && !order.getCreatedAt().equals("N/A")) {
            holder.textOrderDate.setText(order.getCreatedAt()); // Hiển thị trực tiếp chuỗi ngày đã trả về từ API
        } else {
            holder.textOrderDate.setText("N/A");
            Log.w("ORDER_ADAPTER", "⚠️ Đơn hàng #" + order.getId() + " không có ngày tạo!");
        }

        // ✅ Xử lý sự kiện click để mở OrderDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Log.d("ORDER_ADAPTER", "🖱️ Click vào đơn hàng #" + order.getId());
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("ORDER_ID", order.getId()); // Truyền Order ID qua Intent
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        int count = (orderList != null) ? orderList.size() : 0;
        Log.d("ORDER_ADAPTER", "📊 Tổng số item trong RecyclerView: " + count);
        return count;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId, textOrderStatus, textOrderTotal, textOrderDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            textOrderStatus = itemView.findViewById(R.id.text_order_status);
            textOrderTotal = itemView.findViewById(R.id.text_order_total);
            textOrderDate = itemView.findViewById(R.id.text_order_date);
        }
    }
}
