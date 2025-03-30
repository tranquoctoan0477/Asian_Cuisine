package com.example.appasiancuisine.presenter;

import com.example.appasiancuisine.data.dto.OrderDTO;
import java.util.List;

public interface OrderContract {
    interface View {
        void showOrders(List<OrderDTO> orderList);
        void showError(String message);
    }

    interface Presenter {
        void getOrders(String token);
    }
}
