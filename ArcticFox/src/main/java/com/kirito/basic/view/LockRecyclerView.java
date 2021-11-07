package com.kirito.basic.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Finger
 */
public class LockRecyclerView extends RecyclerView {

    private boolean lock = false;

    public LockRecyclerView(Context context) {
        super(context);

    }

    public LockRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!lock) {

            return super.onTouchEvent(ev);
        } else {
            return true;
        }

    }


    /**
     * @return the lock
     */
    public boolean isLock() {
        return lock;
    }

    /**
     * @param lock the lock to set
     */
    public void setLock(boolean lock) {
        this.lock = lock;
    }
}

