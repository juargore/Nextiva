package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import androidx.viewpager.widget.ViewPager

class DisableScrollViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    override fun canScroll(v: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (v is SeekBar) {
            return true
        }

        return super.canScroll(v, checkV, dx, x, y)
    }
}