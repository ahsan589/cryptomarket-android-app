package com.example.cryptomarket;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class PageTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.85f; // Minimum scale factor
    private static final float MAX_ROTATION = 15f; // Maximum rotation angle

    @Override
    public void transformPage(@NonNull View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        if (position < -1) { // [-Infinity,-1)
            page.setAlpha(0f);
        } else if (position <= 1) { // [-1,1]
            // Adjust alpha based on position
            page.setAlpha(1 - Math.abs(position));

            // Horizontal translation
            float translationX = position * pageWidth;
            page.setTranslationX(translationX);

            // Scaling effect
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

            // Rotation effect
            float rotation = MAX_ROTATION * position;
            page.setRotationY(rotation);

            // Depth effect: change the z-index of the page
            page.setTranslationZ(-Math.abs(position));

            // Parallax effect: adjust the translation based on the scroll position
            page.setTranslationY(position * (pageHeight / 2));
        } else { // (1,+Infinity]
            page.setAlpha(0f);
        }
    }
}
