package com.example.appasiancuisine.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.OrderAdapter;
import com.example.appasiancuisine.data.dto.OrderDTO;
import com.example.appasiancuisine.presenter.OrderContract;
import com.example.appasiancuisine.presenter.OrderPresenter;
import com.example.appasiancuisine.utils.PreferenceManager;

import java.util.List;

public class OrderFragment extends Fragment implements OrderContract.View {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private OrderPresenter orderPresenter;

    public OrderFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("ORDER_FRAGMENT", "🔍 OrderFragment onViewCreated() được gọi thành công!");

        try {
            // 1️⃣ Ánh xạ RecyclerView
            recyclerView = view.findViewById(R.id.recycler_orders);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Log.d("ORDER_FRAGMENT", "✅ RecyclerView đã được khởi tạo thành công.");

            // 2️⃣ Khởi tạo Presenter
            orderPresenter = new OrderPresenter(this);
            Log.d("ORDER_FRAGMENT", "✅ OrderPresenter đã được khởi tạo thành công.");

            // 3️⃣ Lấy token người dùng từ PreferenceManager
            PreferenceManager preferenceManager = new PreferenceManager(requireContext());
            String token = preferenceManager.getAccessToken();
            Log.d("ORDER_FRAGMENT", "🔑 Token hiện tại: " + token);

            if (token == null || token.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
                Log.e("ORDER_FRAGMENT", "❌ Không tìm thấy token hoặc token rỗng.");
            } else {
                // 4️⃣ Gọi API lấy danh sách đơn hàng
                Log.d("ORDER_FRAGMENT", "🔍 Gọi API lấy danh sách đơn hàng...");
                orderPresenter.getOrders(token);
            }
        } catch (Exception e) {
            Log.e("ORDER_FRAGMENT", "❌ Lỗi khi khởi tạo OrderFragment: " + e.getMessage(), e);
        }
    }

    @Override
    public void showOrders(List<OrderDTO> orderList) {
        try {
            if (orderList == null) {
                Log.e("ORDER_LIST", "❌ Danh sách đơn hàng nhận được là NULL.");
                Toast.makeText(getContext(), "Lỗi: Danh sách đơn hàng không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("ORDER_LIST", "✅ Số lượng đơn hàng: " + orderList.size());

            if (orderList.isEmpty()) {
                Toast.makeText(getContext(), "Không có đơn hàng nào!", Toast.LENGTH_SHORT).show();
            } else {
                // Log từng đơn hàng để kiểm tra
                for (OrderDTO order : orderList) {
                    if (order != null) {
                        Log.d("ORDER_DATA", "📦 Đơn hàng ID: " + order.getId() +
                                ", Trạng thái: " + order.getStatus() +
                                ", Tổng: " + order.getTotal() +
                                ", Ngày tạo: " + order.getCreatedAt());
                    } else {
                        Log.e("ORDER_DATA", "❌ Một đối tượng OrderDTO là NULL.");
                    }
                }

                // Khởi tạo Adapter và gán cho RecyclerView
                try {
                    orderAdapter = new OrderAdapter(getContext(), orderList);
                    recyclerView.setAdapter(orderAdapter);
                    Log.d("RECYCLER_VIEW", "✅ Adapter đã được set. Số lượng item: " + orderList.size());
                } catch (Exception e) {
                    Log.e("RECYCLER_VIEW_ERROR", "❌ Lỗi khi khởi tạo Adapter: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Lỗi khi hiển thị danh sách đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("SHOW_ORDERS_ERROR", "❌ Lỗi khi hiển thị đơn hàng: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khi hiển thị đơn hàng!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showError(String message) {
        Log.e("ORDER_ERROR", "❌ Lỗi: " + message);
        Toast.makeText(getContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
    }
}
