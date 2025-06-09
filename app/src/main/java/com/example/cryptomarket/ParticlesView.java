package com.example.cryptomarket;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;  // Correct import

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticlesView extends View {
    private static final int PARTICLE_COUNT = 30;
    private final List<Particle> particles = new ArrayList<>();
    private final Paint paint = new Paint();
    private ValueAnimator animator;
    private boolean isAnimating = false;

    public ParticlesView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint.setColor(Color.argb(38, 255, 255, 255));
        paint.setAntiAlias(true);

        Random random = new Random();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new Particle(
                    random.nextFloat() * 100,
                    random.nextFloat() * 100,
                    random.nextFloat() * 3 + 2
            ));
        }
    }

    public void startAnimation() {
        if (isAnimating) return;

        isAnimating = true;
        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(3000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(animation -> invalidate());
        animator.start();
    }

    public void stopAnimation() {
        if (animator != null) {
            animator.cancel();
            isAnimating = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isAnimating) return;

        float progress = animator.getAnimatedFraction();
        for (Particle particle : particles) {
            float y = particle.baseY - (progress * 50);
            canvas.drawCircle(
                    particle.x * getWidth() / 100,
                    y * getHeight() / 100,
                    particle.size,
                    paint
            );
        }
    }

    private static class Particle {
        final float x;
        final float baseY;
        final float size;

        Particle(float x, float y, float size) {
            this.x = x;
            this.baseY = y;
            this.size = size;
        }
    }
}
