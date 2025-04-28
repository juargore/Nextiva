package com.nextiva.nextivaapp.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatEditText;

public class ImeKeyEventEditText extends AppCompatEditText {

    private OnImeKeyEventEditTextListener mOnImeKeyEventEditTextListener;

    public ImeKeyEventEditText(Context context) {
        super(context);
    }

    public ImeKeyEventEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImeKeyEventEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnImeKeyEventEditTextListener != null) {
                mOnImeKeyEventEditTextListener.onImeKeyEvent(this, keyCode);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnImeKeyEventEditTextListener(OnImeKeyEventEditTextListener listener) {
        mOnImeKeyEventEditTextListener = listener;
    }

    public interface OnImeKeyEventEditTextListener {
        void onImeKeyEvent(ImeKeyEventEditText view, int keyCode);
    }
}
