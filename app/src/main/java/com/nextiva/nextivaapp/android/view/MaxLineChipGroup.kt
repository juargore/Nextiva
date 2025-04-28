package com.nextiva.nextivaapp.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.nextiva.nextivaapp.android.R

// Based on https://github.com/Tiarait/ExtendedChipGroup

class MaxLineChipGroup : ChipGroup {
    private val DEF_STYLE_RES = com.google.android.material.R.style.Widget_MaterialComponents_ChipGroup

    private var maxRow = Int.MAX_VALUE
    private var maxRowDef = Int.MAX_VALUE

    private var lastChipsList: ArrayList<String> = ArrayList()
    private var mLineSpacing = 0
    private var mItemSpacing = 0

    private var chipTextColor: Int? = null
    private var chipBackgroundColorId: Int? = null

    private var onLayoutCallback: ((Boolean) -> Unit)? = null
    private var onChipLongClickCallback: ((Chip) -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, com.google.android.material.R.attr.chipGroupStyle)
    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.ChipGroup,
                defStyleAttr,
                DEF_STYLE_RES)

        maxRowDef = a.getColor(R.styleable.ChipGroup_maxRow, maxRow)
        if (maxRowDef > 0) isSingleLine = false

        maxRow = maxRowDef

        val array = context.theme.obtainStyledAttributes(attrs, com.google.android.material.R.styleable.FlowLayout, 0, 0)
        mLineSpacing = array.getDimensionPixelSize(com.google.android.material.R.styleable.FlowLayout_lineSpacing, 0)
        mItemSpacing = array.getDimensionPixelSize(com.google.android.material.R.styleable.FlowLayout_itemSpacing, 0)

        a.recycle()
    }

    fun setMaxRows(rowCount: Int) {
        maxRow = rowCount
        maxRowDef = rowCount
        update(null, onChipLongClickCallback)
    }

    fun update(onLayoutCallback: ((Boolean) -> Unit)?, onChipLongClickCallback: ((Chip) -> Unit)?) {
        setChips(ArrayList(lastChipsList),
                chipTextColor ?: -1,
                chipBackgroundColorId ?: -1,
                onLayoutCallback,
                onChipLongClickCallback)
    }

    fun setChips(text: List<String>, textColor: Int, backgroundColorId: Int, onLayoutCallback: ((Boolean) -> Unit)?,
                 onChipLongClickCallback: ((Chip) -> Unit)?) {
        this.onChipLongClickCallback = onChipLongClickCallback
        this.onLayoutCallback = onLayoutCallback
        chipTextColor = textColor
        chipBackgroundColorId = backgroundColorId

        removeAllViews()
        lastChipsList.clear()
        for (string in text) {
            if (string.isNotEmpty()) {
                val chip = Chip(context)
                chip.text = string
                chip.isSelected = false
                chip.isCheckable = false
                chip.setTextColor(chipTextColor ?: -1)
                chip.setChipBackgroundColorResource(R.color.connectGrey03)
                chip.contentDescription = context.getString(R.string.connect_contact_details_team_chip_content_description, string)
                chip.setOnLongClickListener {
                    onChipLongClickCallback?.let { it(chip) }
                    true
                }
                lastChipsList.add(string)
                addView(chip)
            }
        }
    }

    private var row = 0

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount == 0) {
            row = 0
            return
        }
        row = 1

        val isRtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
        val paddingStart = if (isRtl) paddingRight else paddingLeft
        val paddingEnd = if (isRtl) paddingLeft else paddingRight
        var childStart = paddingStart
        var childTop = paddingTop
        var childBottom = childTop
        var childEnd: Int

        val maxChildEnd = right - left - paddingEnd

        for (i in 0 until childCount) {
            if (getChildAt(i) != null) {
                val child = getChildAt(i) as Chip
                if (child.visibility == View.GONE) {
                    child.setTag(com.google.android.material.R.id.row_index_key, -1)
                    continue
                }

                val lp = child.layoutParams
                var startMargin = 0
                var endMargin = 0

                if (lp is MarginLayoutParams) {
                    startMargin = MarginLayoutParamsCompat.getMarginStart(lp)
                    endMargin = MarginLayoutParamsCompat.getMarginEnd(lp)
                }

                childEnd = childStart + startMargin + child.measuredWidth

                if (!isSingleLine && (childEnd > maxChildEnd)) {
                    childStart = paddingStart
                    childTop = childBottom + mLineSpacing

                    if (row == maxRow && (i - 1) > 0) {
                        (getChildAt(i - 1) as Chip).text = (getChildAt(i - 1) as Chip).text
                    }

                    row++
                }

                child.visibility = if (row > maxRow) View.GONE else View.VISIBLE
                child.setTag(com.google.android.material.R.id.row_index_key, row - 1)
                childEnd = childStart + startMargin + child.measuredWidth
                childBottom = childTop + child.measuredHeight

                if (isRtl) {
                    child.layout(
                        maxChildEnd - childEnd,
                        childTop,
                        maxChildEnd - childStart - startMargin,
                        childBottom
                    )
                } else {
                    child.layout(childStart + startMargin, childTop, childEnd, childBottom)
                }

                childStart += startMargin + endMargin + child.measuredWidth + mItemSpacing
            }
        }

        onLayoutCallback?.let { it(row > maxRow) }
        onLayoutCallback = null
    }
}