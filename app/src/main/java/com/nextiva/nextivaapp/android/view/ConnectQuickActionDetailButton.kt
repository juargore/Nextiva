package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

class ConnectQuickActionDetailButton: FontTextView {
    constructor(context: Context): super(context, null, R.style.ConnectContactDetailQuickActionButton)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs, R.style.ConnectContactDetailQuickActionButton)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.style.ConnectContactDetailQuickActionButton): super(context, attrs, defStyleAttr)

    fun setEnabled(enabled: Boolean, onClickListener: OnClickListener?) {
        isEnabled = enabled

        if (enabled) {
            setOnClickListener(onClickListener)
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.connectWhite))
            setTextColor(ContextCompat.getColor(context, R.color.connectPrimaryBlue))

        } else {
            setOnClickListener(null)
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.connectGrey03))
            setTextColor(ContextCompat.getColor(context, R.color.connectGrey09))
        }
    }
}