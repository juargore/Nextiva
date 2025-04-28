package com.nextiva.nextivaapp.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by Thaddeus Dannar on 10/7/18.
 */
public class ActiveCallViewPager extends ViewPager {
    private final boolean enabled;

    public ActiveCallViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return performClick() && super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return this.enabled && super.performClick();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.enabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean executeKeyEvent(@NonNull KeyEvent event) {
        return this.enabled && super.executeKeyEvent(event);
    }

}
