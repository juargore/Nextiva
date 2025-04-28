package com.nextiva.nextivaapp.android.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R

class HorizontalDottedProgress: LinearLayout {
    private lateinit var img: Array<ImageView?>
    private val circle = GradientDrawable()
    private val OBJECT_SIZE = 3
    private val DURATION = 1200
    private var animator: Array<AnimatorSet?> = arrayOfNulls(OBJECT_SIZE)

    constructor(context: Context?): super(context)

    constructor(context: Context?, @Nullable attrs: AttributeSet?): super(context, attrs) {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setLayoutParams(layoutParams)
        initView()
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)/**/

    private fun initView() {
        val color = (background as? ColorDrawable)?.color ?: ContextCompat.getColor(context, R.color.connectGrey08)

        setBackgroundColor(Color.TRANSPARENT)
        removeAllViews()
        img = arrayOfNulls(OBJECT_SIZE)
        circle.shape = GradientDrawable.OVAL
        circle.setColor(color)
        circle.setSize(10, 10)
        val layoutParams2 = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams2.weight = 1F
        val rel = arrayOfNulls<LinearLayout>(OBJECT_SIZE)
        for (i in 0 until OBJECT_SIZE) {
            rel[i] = LinearLayout(context)

            rel[i]?.apply {
                gravity = Gravity.CENTER
                layoutParams = layoutParams2

                img[i] = ImageView(context)
                img[i]?.apply {
                    setImageDrawable(circle)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }

                addView(img[i])
            }

            addView(rel[i])
        }
    }

    var onLayoutReach = false

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!onLayoutReach) {
            onLayoutReach = true
            animateView()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        for (i in 0 until OBJECT_SIZE) {
            animator[i]?.apply {
                if (isRunning) {
                    removeAllListeners()
                    end()
                    cancel()
                }
            }
        }
    }

    private fun animateView() {
        animator = arrayOfNulls(OBJECT_SIZE)
        for (i in 0 until OBJECT_SIZE) {
            val scaleX = PropertyValuesHolder.ofFloat(SCALE_X, 4f)
            val scaleY = PropertyValuesHolder.ofFloat(SCALE_Y, 4f)
            val reverseScaleX = PropertyValuesHolder.ofFloat(SCALE_X, -4f)
            val reverseScaleY = PropertyValuesHolder.ofFloat(SCALE_Y, -4f)

            val forwardAnimator = ObjectAnimator.ofPropertyValuesHolder(img[i], scaleX, scaleY)
            val reverseAnimator = ObjectAnimator.ofPropertyValuesHolder(img[i], reverseScaleX, reverseScaleY)
            reverseAnimator.startDelay = 300

            val togetherAnimatorSet = AnimatorSet()
            togetherAnimatorSet.playSequentially(forwardAnimator, reverseAnimator)
            animator[i] = togetherAnimatorSet

            animator[i]?.apply {
                duration = DURATION.toLong()
                startDelay = (DURATION * i / 4).toLong()
                start()
            }
        }
        animator[OBJECT_SIZE - 1]?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animateView()
            }
        })
    }
}