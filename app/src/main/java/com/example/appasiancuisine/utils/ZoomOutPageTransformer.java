package com.example.appasiancuisine.utils;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        final float MIN_SCALE = 0.85f;
        final float MIN_ALPHA = 0.5f;

        if (position < -1) {
            page.setAlpha(0f);
        } else if (position <= 1) {
            float scale = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float alpha = Math.max(MIN_ALPHA, 1 - Math.abs(position));

            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setAlpha(alpha);
        } else {
            page.setAlpha(0f);
        }
    }
}

