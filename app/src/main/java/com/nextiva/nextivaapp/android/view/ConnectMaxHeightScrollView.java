package com.nextiva.nextivaapp.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.widget.NestedScrollView;

import com.nextiva.nextivaapp.android.R;

public class ConnectMaxHeightScrollView extends NestedScrollView {
    private int maxHeight;
    private boolean isMaxHeightSet = false;
    private boolean autoScrollDown = true;

    public ConnectMaxHeightScrollView(Context context) {
        super(context);
    }

    public ConnectMaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public ConnectMaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView);
            //200 is a defualt value
            final int defaultHeight = 272;
            maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.MaxHeightScrollView_maxHeight, defaultHeight);
            autoScrollDown = styledAttrs.getBoolean(R.styleable.MaxHeightScrollView_autoScrollDown, true);
            styledAttrs.recycle();
        }
    }

    public void setMaxHeight(float height) {
        maxHeight = Math.round(height);
        isMaxHeightSet = true;
    }

    public boolean isMaxHeightSet() {
        return isMaxHeightSet;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (autoScrollDown) {
            post(() -> fullScroll(View.FOCUS_DOWN));
        }
    }

}
