package com.example.appasiancuisine.presenter;

import android.os.AsyncTask;
import android.util.Log;

import com.example.appasiancuisine.data.dto.HomeResponse;
import com.example.appasiancuisine.utils.AppConfig;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomePresenter implements HomeContract.Presenter {

    private final HomeContract.View view;

    public HomePresenter(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void fetchHomeData(String searchQuery, int page, int limit) {
        new FetchHomeDataTask().execute(searchQuery, String.valueOf(page), String.valueOf(limit));
    }

    private class FetchHomeDataTask extends AsyncTask<String, Void, HomeResponse> {

        private String errorMessage = null;

        @Override
        protected HomeResponse doInBackground(String... params) {
            try {
                String search = params[0];
                int page = Integer.parseInt(params[1]);
                int limit = Integer.parseInt(params[2]);

                // Cấu hình URL với các tham số phân trang và tìm kiếm
                String apiUrl = AppConfig.Home_URL + "?page=" + page + "&limit=" + limit;
                if (search != null && !search.isEmpty()) {
                    apiUrl += "&search=" + search;
                }

                Log.d("API_DEBUG", "Gọi API: " + apiUrl);

                // Tạo kết nối HTTP
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000); // Thêm timeout để tránh treo khi kết nối lâu

                int responseCode = conn.getResponseCode();
                Log.d("API_DEBUG", "Mã trạng thái HTTP: " + responseCode);

                // Kiểm tra xem API trả về thành công không (Mã trạng thái 200)
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    errorMessage = "API trả về lỗi, mã trạng thái: " + responseCode;
                    return null;
                }

                // Đọc dữ liệu trả về từ API
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                reader.close();

                String json = jsonBuilder.toString();
                Log.d("API_DEBUG", "JSON trả về: " + json);

                // Chuyển đổi JSON thành đối tượng HomeResponse
                return new Gson().fromJson(json, HomeResponse.class);

            } catch (Exception e) {
                errorMessage = e.getMessage();
                Log.e("API_ERROR", "Lỗi khi gọi API Home", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(HomeResponse result) {
            if (result != null) {
                // Dữ liệu đã tải thành công, gọi hàm callback để thông báo cho view
                view.onHomeDataLoaded(result);
            } else {
                // Nếu có lỗi, hiển thị thông báo lỗi
                view.onHomeDataError(errorMessage != null ? errorMessage : "Lỗi không xác định");
            }
        }
    }
}
