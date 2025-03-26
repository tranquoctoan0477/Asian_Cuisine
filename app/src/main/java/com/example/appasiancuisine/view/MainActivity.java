package com.example.appasiancuisine.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.presenter.ReloadableFragment;
import com.example.appasiancuisine.view.fragments.HomeFragment;
import com.example.appasiancuisine.view.fragments.MenuFragment;
import com.example.appasiancuisine.view.fragments.BookingFragment;
import com.example.appasiancuisine.view.fragments.OrderFragment;
import com.example.appasiancuisine.view.fragments.ProfileFragment;

import me.ibrahimsn.lib.SmoothBottomBar;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private SmoothBottomBar bottomBar;

    // Giữ instance các Fragment để không tạo lại
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment menuFragment = new MenuFragment();
    private final Fragment bookingFragment = new BookingFragment();
    private final Fragment orderFragment = new OrderFragment();
    private final Fragment profileFragment = new ProfileFragment();

    private Fragment activeFragment = homeFragment;  // Fragment hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomBar = findViewById(R.id.bottom_nav);

        // Khởi tạo tất cả fragment, chỉ hiển thị homeFragment
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.main_frame, profileFragment, "5").hide(profileFragment)
                .add(R.id.main_frame, orderFragment, "4").hide(orderFragment)
                .add(R.id.main_frame, bookingFragment, "3").hide(bookingFragment)
                .add(R.id.main_frame, menuFragment, "2").hide(menuFragment)
                .add(R.id.main_frame, homeFragment, "1")
                .commit();

        bottomBar.setOnItemSelectedListener(new Function1<Integer, Unit>() {
            @Override
            public Unit invoke(Integer position) {
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
                        // Nếu tab đang được chọn lại → gọi reload
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
            }
        });
        // thêm listener reload khi bấm lại item hiện tại
        bottomBar.setOnItemReselectedListener(new Function1<Integer, Unit>() {
            @Override
            public Unit invoke(Integer position) {
                Fragment currentFragment = null;

                switch (position) {
                    case 0: currentFragment = homeFragment; break;
                    case 1: currentFragment = menuFragment; break;
                    case 2: currentFragment = bookingFragment; break;
                    case 3: currentFragment = orderFragment; break;
                    case 4: currentFragment = profileFragment; break;
                }

                if (currentFragment instanceof ReloadableFragment) {
                    ((ReloadableFragment) currentFragment).reload();
                }

                return Unit.INSTANCE;
            }
        });

    }

}
