package com.example.appasiancuisine.presenter;

public interface LoginContract {

    interface View {
        void onLoginSuccess(String accessToken, String refreshToken, String username, String email, String role);
        void onLoginFailure(String message);
        void showLoading();
        void hideLoading();
    }

    interface Presenter {
        void login(String email, String password);
    }
}
