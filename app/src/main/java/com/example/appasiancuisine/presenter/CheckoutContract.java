package com.example.appasiancuisine.presenter;

import java.util.List;
import com.example.appasiancuisine.data.dto.CheckoutItemDTO;

public interface CheckoutContract {

    interface View {
        void onCheckoutSuccess(String message);
        void onCheckoutError(String error);
    }

    interface Presenter {
        void checkout(String address, String phone, String note, String voucherCode, List<CheckoutItemDTO> items, String accessToken);
    }
}
