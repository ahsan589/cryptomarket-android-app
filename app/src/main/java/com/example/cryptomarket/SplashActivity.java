package com.example.cryptomarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    private LinearLayout mainContent;
    private TextView changingWord;
    private RelativeLayout buttonContainer;
    private Button getStartedButton;
    private View buttonGlow;
    private LottieAnimationView lottieAnimation;

    private final String[] words = {"Analyze", "Track", "Insight", "Update", "Grow"};
    private final int[] colors = {
            R.color.colorPrimary, R.color.teal_200,
            R.color.teal_700, R.color.orange, R.color.red
    };
    private int currentWordIndex = 0;

    private List<View> particles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        mainContent = findViewById(R.id.mainContent);
        changingWord = findViewById(R.id.changingWord);
        buttonContainer = findViewById(R.id.buttonContainer);
        getStartedButton = findViewById(R.id.getStartedButton);
        buttonGlow = findViewById(R.id.buttonGlow);

        // Set up animations
        setupAnimations();

        // Word cycling
        setupWordCycling();

        // Button click
        getStartedButton.setOnClickListener(v -> animateExit());

        // Create particles
        createParticles();
    }

    private void setupAnimations() {
        // Fade and scale animation for main content
        mainContent.setAlpha(0f);
        mainContent.setScaleX(0.8f);
        mainContent.setScaleY(0.8f);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(mainContent, "alpha", 0f, 1f);
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(mainContent, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(mainContent, "scaleY", 0.8f, 1f);

        fadeAnim.setDuration(1000);
        scaleXAnim.setDuration(1000);
        scaleYAnim.setDuration(1000);

        animatorSet.playTogether(fadeAnim, scaleXAnim, scaleYAnim);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                // Button animation
                buttonContainer.setVisibility(View.VISIBLE);
                buttonContainer.setTranslationY(50f);
                buttonContainer.setAlpha(0f);

                ObjectAnimator buttonFade = ObjectAnimator.ofFloat(buttonContainer, "alpha", 0f, 1f);
                ObjectAnimator buttonTranslate = ObjectAnimator.ofFloat(buttonContainer, "translationY", 50f, 0f);

                AnimatorSet buttonAnimSet = new AnimatorSet();
                buttonAnimSet.playTogether(buttonFade, buttonTranslate);
                buttonAnimSet.setDuration(500);
                buttonAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animatorSet.start();
    }

    private void setupWordCycling() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentWordIndex = (currentWordIndex + 1) % words.length;
                changingWord.setText(words[currentWordIndex]);
                changingWord.setTextColor(ContextCompat.getColor(SplashActivity.this, colors[currentWordIndex]));
                handler.postDelayed(this, 500);
            }
        }, 500);
    }

    private void animateExit() {
        // Fade out animation
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mainContent, "alpha", 1f, 0f);
        fadeOut.setDuration(500);

        // Button exit animation
        ObjectAnimator buttonFadeOut = ObjectAnimator.ofFloat(buttonContainer, "alpha", 1f, 0f);
        ObjectAnimator buttonTranslate = ObjectAnimator.ofFloat(buttonContainer, "translationY", 0f, 50f);

        AnimatorSet exitAnimSet = new AnimatorSet();
        exitAnimSet.playTogether(fadeOut, buttonFadeOut, buttonTranslate);
        exitAnimSet.setDuration(500);
        exitAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                // Navigate to main activity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        exitAnimSet.start();
    }

    private void createParticles() {
        // Get the parent view of mainContent to add particles to
        ViewGroup rootLayout = (ViewGroup) mainContent.getParent();
        Random random = new Random();

        for (int i = 0; i < 30; i++) {
            View particle = new View(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    dpToPx(random.nextInt(3) + 2), // size between 2-5dp
                    dpToPx(random.nextInt(3) + 2)
            );

            params.leftMargin = dpToPx(random.nextInt(360)); // x position (0-100%)
            params.topMargin = dpToPx(random.nextInt(640));  // y position (0-100%)

            particle.setLayoutParams(params);
            particle.setBackground(ContextCompat.getDrawable(this, R.drawable.particle_bg));
            particle.setAlpha(0f);

            rootLayout.addView(particle);
            particles.add(particle);

            // Animate particle
            animateParticle(particle);
        }
    }

    private void animateParticle(View particle) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f, 0f);
        animator.setDuration(3000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            particle.setAlpha(value);
            particle.setTranslationY(-value * dpToPx(50));
        });
        animator.start();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}