package com.nextiva.nextivaapp.android.util.extensions

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText

private const val COMPOUND_DRAWABLE_RIGHT_INDEX = 2

fun EditText.onFocusChanged(onFocusChanged: () -> Unit) {
    this.setOnFocusChangeListener { _, _ -> onFocusChanged.invoke() }
}

fun EditText.makeClearableEditText(
        onFocusChangedListener: (() -> Unit)?,
        clearDrawable: Drawable) {

    val updateRightDrawable = {
        this.setCompoundDrawables(null, null,
                if (text.isNotEmpty() && this.hasFocus()) clearDrawable else null,
                null)
    }

    updateRightDrawable()

    this.afterTextChanged {
        updateRightDrawable()
    }

    this.onFocusChanged {
        onFocusChangedListener?.invoke()
        updateRightDrawable()
    }

    this.onRightDrawableClicked {
        this.text.clear()
        this.setCompoundDrawables(null, null, null, null)
        this.requestFocus()
    }
}

fun EditText.makeClearableEditText(onFocusChangedListener: (() -> Unit)?) {
    compoundDrawables[COMPOUND_DRAWABLE_RIGHT_INDEX]?.let { clearDrawable ->
        makeClearableEditText(onFocusChangedListener, clearDrawable)
    }
}

fun EditText.makeClearableEditText() {
    compoundDrawables[COMPOUND_DRAWABLE_RIGHT_INDEX]?.let { clearDrawable ->
        makeClearableEditText(null, clearDrawable)
    }
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is EditText) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}
