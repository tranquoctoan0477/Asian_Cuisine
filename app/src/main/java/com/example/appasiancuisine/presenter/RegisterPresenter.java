package com.example.appasiancuisine.presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.appasiancuisine.utils.ApiClient;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterPresenter implements RegisterContract.Presenter {

    private final RegisterContract.View view;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public RegisterPresenter(RegisterContract.View view) {
        this.view = view;
    }

    @Override
    public void register(String username, String email, String password, String phone, String gender) {
        view.showLoading();

        executorService.execute(() -> {
            try {
                Log.d("DEBUG_REGISTER", "📤 Gửi request đăng ký...");
                JSONObject response = ApiClient.register(username, email, password, phone, gender);

                String message = response.optString("message", "Đăng ký thành công");

                mainHandler.post(() -> {
                    view.hideLoading();
                    view.onRegisterSuccess(message);
                });

            } catch (Exception e) {
                Log.e("DEBUG_REGISTER", "❌ Lỗi khi gọi API đăng ký", e);
                mainHandler.post(() -> {
                    view.hideLoading();
                    view.onRegisterFailure("Đăng ký thất bại: " + e.getMessage());
                });
            }
        });
    }
}
