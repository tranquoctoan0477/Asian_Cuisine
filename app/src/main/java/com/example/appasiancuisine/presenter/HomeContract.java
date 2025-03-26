package com.example.appasiancuisine.presenter;

import com.example.appasiancuisine.data.dto.HomeResponse;

public interface HomeContract {

    interface View {
        void onHomeDataLoaded(HomeResponse response);
        void onHomeDataError(String errorMessage);
    }

    interface Presenter {
        void fetchHomeData(String searchQuery, int page, int limit); // Cập nhật ở đây
    }
}
