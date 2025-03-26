package com.example.appasiancuisine.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.IntroSliderAdapter;
import com.example.appasiancuisine.model.IntroSlide;
import com.example.appasiancuisine.utils.PreferenceManager;
import com.example.appasiancuisine.utils.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private LinearLayout indicatorLayout;
    private Button buttonStart;

    private final List<IntroSlide> slideList = new ArrayList<>();
    private final List<ImageView> indicators = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        viewPager2 = findViewById(R.id.introViewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        buttonStart = findViewById(R.id.buttonStart);

        setupIntroSlides();
        setupIndicators();
        setCurrentIndicator(0);

        // Gắn adapter
        viewPager2.setAdapter(new IntroSliderAdapter(slideList));
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());

        // Xử lý khi chuyển slide
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);

                if (position == slideList.size() - 1) {
                    // Đang ở slide cuối → Hiện nút sau 5s
                    buttonStart.setVisibility(View.VISIBLE);
                    buttonStart.setEnabled(false);
                    handler.postDelayed(() -> buttonStart.setEnabled(true), 5000);
                } else {
                    buttonStart.setVisibility(View.GONE);
                }
            }
        });

        buttonStart.setOnClickListener(v -> {
            // Đánh dấu đã xem Intro
            PreferenceManager prefManager = new PreferenceManager(IntroActivity.this);
            prefManager.setFirstTimeLaunch(false);

            // Chuyển sang LoginActivity
            startActivity(new Intent(IntroActivity.this, AuthChoiceActivity.class));
            finish();
        });
    }

    // Khởi tạo danh sách slider
    private void setupIntroSlides() {
        slideList.add(new IntroSlide(
                R.drawable.logo,
                "Chào mừng đến Sushi Restaurant!",
                "Trải nghiệm ẩm thực Nhật Bản tinh tế với những món sushi ngon nhất."
        ));

        slideList.add(new IntroSlide(
                R.drawable.logo, // thay icon phù hợp
                "Đặt bàn & món ăn chỉ trong vài bước!",
                "Chọn bàn phù hợp, lướt menu hấp dẫn và đặt món ngay trên điện thoại của bạn."
        ));

        slideList.add(new IntroSlide(
                R.drawable.logo, // icon quà/ưu đãi
                "Ưu đãi & Tích điểm hấp dẫn!",
                "Tích điểm mỗi lần đặt món để đổi lấy voucher và ưu đãi đặc biệt."
        ));

        slideList.add(new IntroSlide(
                R.drawable.logo, // icon cảnh báo/quy tắc
                "Một vài lưu ý nhỏ trước khi dùng app",
                "Vui lòng xác nhận đúng giờ đặt bàn, không hủy đơn sát giờ và tuân thủ chính sách nhà hàng."
        ));
    }

    // Tạo các chấm indicator
    private void setupIndicators() {
        for (int i = 0; i < slideList.size(); i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.indicator_inactive); // dot mặc định
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            indicatorLayout.addView(dot);
            indicators.add(dot);
        }
    }

    // Cập nhật trạng thái chấm khi trượt
    private void setCurrentIndicator(int index) {
        for (int i = 0; i < indicators.size(); i++) {
            indicators.get(i).setImageResource(
                    i == index ? R.drawable.indicator_active : R.drawable.indicator_inactive
            );
        }
    }


}
