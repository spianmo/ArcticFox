package com.kirito.basic.view.headers;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;


public class AuraDrawable extends Renderable {
    private final Drawable drawable;
    private long lastFlicker;

    public AuraDrawable(Drawable drawable, Rect position) {
        super(null, 0, 0);
        drawable.setBounds(position);
        this.drawable = drawable;
        lastFlicker = System.currentTimeMillis();
    }

    @Override
    public void draw(Canvas canvas) {
        drawable.draw(canvas);
    }

    public void update(float deltaTime, float wind) {
        if (lastFlicker + 50 < System.currentTimeMillis()) {
            drawable.setAlpha((int) (255 * (30f + (float) MathHelper.rand.nextInt(25)) / 100f));
            lastFlicker = System.currentTimeMillis();
        }
    }

}
