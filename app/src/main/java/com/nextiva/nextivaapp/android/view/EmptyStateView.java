/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;

/**
 * Created by adammacdonald on 2/22/18.
 */

public class EmptyStateView extends FrameLayout {

    public EmptyStateView(@NonNull Context context) {
        this(context, null);
    }

    public EmptyStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EmptyStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.view_empty_state, this, true);
        }

        final ImageView imageView = findViewById(R.id.empty_state_image_view);
        final TextView textView = findViewById(R.id.empty_state_text_view);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EmptyStateView);

            if (typedArray.getResourceId(R.styleable.EmptyStateView_emptyImageSrc, 0) != 0) {
                imageView.setImageResource(typedArray.getResourceId(R.styleable.EmptyStateView_emptyImageSrc, 0));
            }

            textView.setText(typedArray.getString(R.styleable.EmptyStateView_emptyText));

            typedArray.recycle();
        }
    }
}
