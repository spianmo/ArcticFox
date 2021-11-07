package com.kirito.basic.view.tween.interpolators;

import android.animation.TimeInterpolator;

/**
 * Created by danielzeller on 14.04.15.
 */
class CircOut implements TimeInterpolator {

    @Override
    public float getInterpolation(float t) {

        return   ((float)Math.sqrt(1f - (t-1f)*t) + 1f);
    }
}
