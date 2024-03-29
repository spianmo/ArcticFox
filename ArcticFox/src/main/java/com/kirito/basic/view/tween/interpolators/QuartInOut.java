package com.kirito.basic.view.tween.interpolators;

import android.animation.TimeInterpolator;

/**
 * Created by danielzeller on 09.04.15.
 */
public class QuartInOut implements TimeInterpolator {
    @Override
    public float getInterpolation(float t) {
        if ((t*=2) < 1) return 0.5f*t*t*t*t;
        return -0.5f * ((t-=2)*t*t*t - 2);
    }
}