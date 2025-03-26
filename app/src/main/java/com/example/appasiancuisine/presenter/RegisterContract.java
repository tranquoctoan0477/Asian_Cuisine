package com.example.appasiancuisine.presenter;

public interface RegisterContract {

    interface View {
        void onRegisterSuccess(String message);
        void onRegisterFailure(String message);
        void showLoading();
        void hideLoading();
    }

    interface Presenter {
        void register(String username, String email, String password, String phone, String gender);
    }
}
