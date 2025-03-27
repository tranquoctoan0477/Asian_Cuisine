package com.example.appasiancuisine.view;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.data.dto.HomeResponse;
import com.example.appasiancuisine.presenter.HomeContract;
import com.example.appasiancuisine.presenter.HomePresenter;
import com.example.appasiancuisine.presenter.ReloadableFragment;
import com.example.appasiancuisine.view.fragments.BookingFragment;
import com.example.appasiancuisine.view.fragments.HomeFragment;
import com.example.appasiancuisine.view.fragments.MenuFragment;
import com.example.appasiancuisine.view.fragments.OrderFragment;
import com.example.appasiancuisine.view.fragments.ProfileFragment;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity implements HomeContract.View {

    private SmoothBottomBar bottomBar;
    private final HomeFragment homeFragment = new HomeFragment();
    private final MenuFragment menuFragment = new MenuFragment();
    private final BookingFragment bookingFragment = new BookingFragment();
    private final OrderFragment orderFragment = new OrderFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    private Fragment activeFragment = homeFragment;
    private HomePresenter homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homePresenter = new HomePresenter(this); // Khởi tạo Presenter

        bottomBar = findViewById(R.id.bottom_nav);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.main_frame, profileFragment, "5").hide(profileFragment)
                .add(R.id.main_frame, orderFragment, "4").hide(orderFragment)
                .add(R.id.main_frame, bookingFragment, "3").hide(bookingFragment)
                .add(R.id.main_frame, menuFragment, "2").hide(menuFragment)
                .add(R.id.main_frame, homeFragment, "1")
                .commit();

        // Gọi API lấy dữ liệu ban đầu
        homePresenter.fetchHomeData("", 0, 100); // Tăng limit để đủ sản phẩm và danh mục

        bottomBar.setOnItemSelectedListener((Function1<? super Integer, Unit>) position -> {
            Fragment selected = null;
            switch (position) {
                case 0: selected = homeFragment; break;
                case 1: selected = menuFragment; break;
                case 2: selected = bookingFragment; break;
                case 3: selected = orderFragment; break;
                case 4: selected = profileFragment; break;
            }

            if (selected != null) {
                if (selected == activeFragment && selected instanceof ReloadableFragment) {
                    ((ReloadableFragment) selected).reload();
                } else if (selected != activeFragment) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .hide(activeFragment)
                            .show(selected)
                            .commit();
                    activeFragment = selected;
                }
            }

            return Unit.INSTANCE;
        });

        bottomBar.setOnItemReselectedListener(position -> {
            Fragment current = null;
            switch (position) {
                case 0: current = homeFragment; break;
                case 1: current = menuFragment; break;
                case 2: current = bookingFragment; break;
                case 3: current = orderFragment; break;
                case 4: current = profileFragment; break;
            }
            if (current instanceof ReloadableFragment) {
                ((ReloadableFragment) current).reload();
            }
            return Unit.INSTANCE;
        });
    }

    // ========================
    // Gửi dữ liệu đến 2 fragment
    // ========================
    @Override
    public void onHomeDataLoaded(HomeResponse response) {
        Log.d("MainActivity", "Dữ liệu API đã load. Truyền vào HomeFragment và MenuFragment");
        homeFragment.setupDataFromApi(response);
        menuFragment.setupDataFromApi(response);
    }

    @Override
    public void onHomeDataError(String errorMessage) {
        Log.e("MainActivity", "Lỗi khi load dữ liệu: " + errorMessage);
    }
}
