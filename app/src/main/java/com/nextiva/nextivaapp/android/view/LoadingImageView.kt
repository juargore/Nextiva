package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView

import android.widget.ProgressBar

import android.widget.RelativeLayout

class LoaderImageView : RelativeLayout {
    private var mContext: Context? = null
    var progressBar: ProgressBar? = null
    var imageView: ImageView? = null

    private fun instantiate(_context: Context, attrSet: AttributeSet) {
        mContext = _context
        imageView = ImageView(context, attrSet)
        progressBar = ProgressBar(context, attrSet)
        progressBar?.isIndeterminate = true
        addView(imageView)
        addView(progressBar)

        val imageViewLayoutParams = imageView?.layoutParams as LayoutParams
        imageViewLayoutParams.addRule(CENTER_IN_PARENT, TRUE)
        imageView?.layoutParams = imageViewLayoutParams

        val progressBarLayoutParams = progressBar?.layoutParams as LayoutParams
        progressBarLayoutParams.addRule(CENTER_IN_PARENT, TRUE)
        progressBar?.layoutParams = progressBarLayoutParams

        imageView?.visibility = View.VISIBLE
        this.gravity = Gravity.CENTER
    }

    // ...
    // Then, play with this method to show or hide your progressBar
    constructor(context: Context, attrSet: AttributeSet) : super(context, attrSet) {
        instantiate(context, attrSet)
    }

    fun showProgress() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgress() {
        progressBar?.visibility = View.GONE
    }
}