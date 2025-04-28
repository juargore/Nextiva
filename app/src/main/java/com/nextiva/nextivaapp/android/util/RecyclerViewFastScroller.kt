package com.nextiva.nextivaapp.android.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Fast scroller for RecyclerView.
 */
class RecyclerViewFastScroller(private val context: Context, private val settingsManager: SettingsManager, val isConnect: Boolean)
    : RecyclerView.ItemDecoration(), RecyclerView.OnItemTouchListener {

    companion object {
        // Scroll thumb not showing
        private const val STATE_HIDDEN = 0

        // Scroll thumb visible and moving along with the scrollbar
        private const val STATE_VISIBLE = 1

        // Scroll thumb being dragged by user
        private const val STATE_DRAGGING = 2

        private const val ANIMATION_STATE_OUT = 0
        private const val ANIMATION_STATE_FADING_IN = 1
        private const val ANIMATION_STATE_IN = 2
        private const val ANIMATION_STATE_FADING_OUT = 3

        private const val SHOW_DURATION_MS = 200
        private const val HIDE_DURATION_MS = 500
        private const val HIDE_DELAY_AFTER_VISIBLE_MS = 1500
        private const val HIDE_DELAY_AFTER_DRAGGING_MS = 1500
    }

    private fun convertDpToPx(dimension: Int) = ((context.resources.getDimension(dimension) / context.resources.displayMetrics.density)
            * this.recyclerView.resources.displayMetrics.density).roundToInt()

    private lateinit var recyclerView: RecyclerView

    private val colorNormal = ContextCompat.getColor(context, if (isConnect) R.color.connectPrimaryGrey else R.color.nextivaGrey)
    private val colorPressed = ContextCompat.getColor(context, if (isConnect) R.color.connectPrimaryGrey else if (ApplicationUtil.isNightModeEnabled(context, settingsManager)) R.color.nextivaOrange else R.color.nextivaPrimaryBlue)

    private var thumbWidth: Int = 0
    private var touchWidth: Int = 0
    private var touchMinHeight: Int = 0

    private var thumbY = 0
    private var thumbHeight = 0
    private var thumbScrollRange = 0f

    private var lastTouchY = 0f

    private var recyclerViewWidth = 0
    private var recyclerViewHeight = 0
    private var scrollableRange = 0 // real scroll range without recyclerView height

    private var currentState = STATE_HIDDEN
    private var needRecompute = true

    private lateinit var layoutManager: LinearLayoutManager

    private var alpha = 1f
    private val showHideAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        addListener(AnimatorListener())
        addUpdateListener(AnimatorUpdater())
    }

    private var animationState = ANIMATION_STATE_OUT
    private val hideRunnable = Runnable {
        hide(HIDE_DURATION_MS)
    }

    private inner class AnimatorUpdater : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
            alpha = valueAnimator.animatedValue as Float
            requestRedraw()
        }
    }

    fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        layoutManager = this.recyclerView.layoutManager as LinearLayoutManager

        thumbWidth = convertDpToPx(if (isConnect) R.dimen.general_view_xxsmall else R.dimen.recycler_view_thumb_width)
        touchWidth = convertDpToPx(R.dimen.general_view_medium)
        touchMinHeight = convertDpToPx(if (isConnect) R.dimen.general_view_xxxxxlarge else R.dimen.general_view_xxlarge)

        with(this.recyclerView) {
            addItemDecoration(this@RecyclerViewFastScroller)
            addOnItemTouchListener(this@RecyclerViewFastScroller)
        }

        this.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, deltaX: Int, deltaY: Int) {
                computeScroll()
            }
        })

        this.recyclerView.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            private fun update() {
                needRecompute = true
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = update()
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = update()
        })
    }

    private fun requestRedraw() = recyclerView.invalidate()

    private fun setState(state: Int) {
        if (state == STATE_HIDDEN) {
            requestRedraw()

        } else {
            show()
        }

        if (currentState == STATE_DRAGGING && state != STATE_DRAGGING) {
            resetHideDelay(HIDE_DELAY_AFTER_DRAGGING_MS)

        } else if (state == STATE_VISIBLE) {
            resetHideDelay(HIDE_DELAY_AFTER_VISIBLE_MS)
        }

        currentState = state
    }

    private fun show() {
        with(showHideAnimator) {
            if (animationState == ANIMATION_STATE_FADING_OUT) {
                cancel()
                animationState = ANIMATION_STATE_OUT
            }

            if (animationState == ANIMATION_STATE_OUT) {
                animationState = ANIMATION_STATE_FADING_IN
                setFloatValues(alpha, 1f)
                duration = SHOW_DURATION_MS.toLong()
                startDelay = 0
                start()
            }
        }
    }

    private fun hide(distance: Int) {
        with(showHideAnimator) {
            if (animationState == ANIMATION_STATE_FADING_IN) {
                cancel()
                animationState = ANIMATION_STATE_IN
            }

            if (animationState == ANIMATION_STATE_IN) {
                animationState = ANIMATION_STATE_FADING_OUT
                setFloatValues(alpha, 0f)
                duration = distance.toLong()
                start()
            }
        }
    }

    private fun cancelHide() {
        recyclerView.removeCallbacks(hideRunnable)
    }

    private fun resetHideDelay(delay: Int) {
        cancelHide()
        recyclerView.postDelayed(hideRunnable, delay.toLong())
    }

    private fun computeScroll() {
        needRecompute = false
        val scrollOffset = recyclerView.computeVerticalScrollOffset()
        val scrollRange = recyclerView.computeVerticalScrollRange()
        scrollableRange = scrollRange - recyclerViewHeight

        if (scrollableRange > 0) {
            val ratio = scrollOffset.toFloat() / scrollableRange
            thumbHeight = max((recyclerViewHeight.toFloat() / scrollRange * recyclerViewHeight).toInt(), touchMinHeight)

            val track = recyclerViewHeight - thumbHeight

            if (track > 0) {
                thumbScrollRange = track.toFloat()
                thumbY = (ratio * thumbScrollRange).toInt()

                if (currentState == STATE_HIDDEN || currentState == STATE_VISIBLE) {
                    setState(STATE_VISIBLE)
                }
                return
            }
        }
        setState(STATE_HIDDEN)
    }

    private fun scrollItems(ratio: Float) {
        recyclerView.adapter?.let {
            val topItem = layoutManager.findFirstVisibleItemPosition()
            val visibleRange = layoutManager.findLastVisibleItemPosition() + 1 - topItem
            val positionInRange = it.itemCount - visibleRange
            val distance = (positionInRange * ratio).roundToInt()
            layoutManager.scrollToPositionWithOffset(topItem + distance, 0)
        }
    }

    private fun scrollPixels(ratio: Float) {
        val distance = (ratio * scrollableRange).toInt()

        if (abs(distance) > recyclerViewHeight) {
            scrollItems(ratio)

        } else {
            recyclerView.scrollBy(0, distance)
        }
    }

    private fun isPointInsideVerticalThumb(x: Float, y: Float): Boolean {
        return ((x >= recyclerViewWidth - touchWidth)
                && y >= thumbY
                && y < (thumbY + thumbHeight))
    }

    private inner class AnimatorListener : AnimatorListenerAdapter() {
        private var canceled = false
        override fun onAnimationEnd(animation: Animator) {
            // cancel is always followed by a new directive, so don't update state
            if (canceled) {
                canceled = false
                return
            }
            if (showHideAnimator.animatedValue as Float == 0f) {
                animationState = ANIMATION_STATE_OUT
                setState(STATE_HIDDEN)
            } else {
                animationState = ANIMATION_STATE_IN
                requestRedraw()
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            canceled = true
        }
    }

    // --------------------------------------------------------------------------------------------
    // RecyclerView.ItemDecoration Methods
    // --------------------------------------------------------------------------------------------
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, recyclerViewState: RecyclerView.State) {
        if (recyclerViewWidth != recyclerView.width || recyclerViewHeight != recyclerView.height) {
            recyclerViewWidth = recyclerView.width
            recyclerViewHeight = recyclerView.height
            // This is due to the different events ordering when keyboard is opened or
            // retracted vs rotate. Hence to avoid corner cases we just disable the
            // scroller when size changed, and wait until the scroll position is recomputed
            // before showing it back.
            setState(STATE_HIDDEN)
            return
        }

        if (needRecompute)
            computeScroll()

        if (animationState != ANIMATION_STATE_OUT && currentState != STATE_HIDDEN) {
            with(canvas) {
                save()
                val left = recyclerViewWidth - thumbWidth - if (isConnect) convertDpToPx(R.dimen.general_view_xsmall) else 0
                val top = thumbY
                clipRect(left, top, left + thumbWidth, top + thumbHeight)
                var color = if (currentState == STATE_DRAGGING) colorPressed else colorNormal

                if (alpha != 1f) {
                    color = (color and 0xffffff) or (((color ushr 24) * alpha).toInt() shl 24)
                }

                drawColor(color)
                restore()
            }
        }
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // RecyclerView.OnItemTouchListener Methods
    // --------------------------------------------------------------------------------------------
    override fun onInterceptTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent): Boolean {
        return when (motionEvent.action) {
            MotionEvent.ACTION_DOWN ->
                if (currentState == STATE_VISIBLE) {
                    if (isPointInsideVerticalThumb(motionEvent.x, motionEvent.y)) {
                        cancelHide()
                        setState(STATE_DRAGGING)
                        requestRedraw()
                        lastTouchY = motionEvent.y
                        true

                    } else {
                        false
                    }

                } else {
                    false
                }

            MotionEvent.ACTION_MOVE ->
                currentState == STATE_DRAGGING

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                if (currentState == STATE_DRAGGING) {
                    setState(STATE_VISIBLE)
                    requestRedraw()
                    true
                } else {
                    false
                }

            else -> false
        }
    }

    override fun onTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent) {
        if (currentState == STATE_HIDDEN) {
            return
        }

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> onInterceptTouchEvent(recyclerView, motionEvent)
            MotionEvent.ACTION_MOVE -> if (currentState == STATE_DRAGGING && thumbScrollRange > 0) {
                show()
                val y = motionEvent.y
                val ratio = (y - lastTouchY) / thumbScrollRange
                lastTouchY = y
                scrollPixels(ratio)
            }
        }
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    // --------------------------------------------------------------------------------------------
}