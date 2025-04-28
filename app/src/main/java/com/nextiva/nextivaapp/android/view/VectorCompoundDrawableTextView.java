/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.nextiva.nextivaapp.android.R;

/**
 * Created by adammacdonald on 2/16/18.
 */

public class VectorCompoundDrawableTextView extends AppCompatTextView {

    public VectorCompoundDrawableTextView(Context context) {
        super(context);
        init(context, null);
    }

    public VectorCompoundDrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VectorCompoundDrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VectorCompoundDrawableTextView, 0, 0);
            try {
                VectorDrawableCompat top = null, bottom = null, left = null, right = null;

                if (ta.getResourceId(R.styleable.VectorCompoundDrawableTextView_drawableTop, 0) != 0) {
                    top = VectorDrawableCompat.create(getResources(), ta.getResourceId(R.styleable.VectorCompoundDrawableTextView_drawableTop, 0), null);
                }

                if (ta.getResourceId(R.styleable.VectorCompoundDrawableTextView_drawableBottom, 0) != 0) {
                    bottom = VectorDrawableCompat.create(getResources(), ta.getResourceId(R.styleable.VectorCompoundDrawableTextView_drawableBottom, 0), null);
                }

                if (ta.getResourceId(R.styleable.VectorCompoundDrawableTextView_drawableLeft, 0) != 0) {
                    left = VectorDrawableCompat.create(getResources(), ta.getResourceId(R.styleable.VectorCompoundDrawableTextView_drawableLeft, 0), null);
                }

                if (ta.getResourceId(R.styleable.VectorCompoundDrawableTextView_drawableRight, 0) != 0) {
                    right = VectorDrawableCompat.create(getResources(), ta.getResourceId(R.styleable.VectorCompoundDrawableTextView_drawableRight, 0), null);
                }

                if (ta.getResourceId(R.styleable.VectorCompoundDrawableTextView_drawableTint, 0) != 0) {
                    final int tintResId = ta.getResourceId(R.styleable.VectorCompoundDrawableTextView_drawableTint, 0);
                    int tintColor = ContextCompat.getColor(getContext(), tintResId);

                    if (top != null) {
                        DrawableCompat.setTint(top, tintColor);
                    }
                    if (bottom != null) {
                        DrawableCompat.setTint(bottom, tintColor);
                    }
                    if (left != null) {
                        DrawableCompat.setTint(left, tintColor);
                    }
                    if (right != null) {
                        DrawableCompat.setTint(right, tintColor);
                    }
                }

                setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);

            } finally {
                ta.recycle();
            }
        }
    }

}