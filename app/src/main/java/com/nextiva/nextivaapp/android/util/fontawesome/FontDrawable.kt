package com.nextiva.nextivaapp.android.util.fontawesome

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.TypedValue
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.FontAwesomeVersion
import kotlin.math.ceil


/**
 * A Drawable object that draws text.
 * A FontDrawable accepts most of the same parameters that can be applied to
 * [android.widget.TextView] for displaying and formatting text.
 *
 *
 * Optionally, a [Path] may be supplied on which to draw the text.
 *
 *
 * A FontDrawable has an intrinsic size equal to that required to draw all
 * the text it has been supplied, when possible.  In cases where a [Path]
 * has been supplied, the caller must explicitly call
 * [setBounds()][.setBounds] to provide the Drawable
 * size based on the Path constraints.
 */
class FontDrawable(
    context: Context,
    faIconRes: Int,
    @Enums.FontAwesomeIconType.Type iconType: Int,
    @Enums.FontAwesomeVersion.Type faVersion: Int = FontAwesomeVersion.FA_V5
) : Drawable() {
    /* Resources for scaling values to the given device */
    private var resources: Resources? = null

    /* Paint to hold most drawing primitives for the text */
    private var textPaint: TextPaint? = null

    /* Layout is used to measure and draw the text */
    private var textLayout: StaticLayout? = null

    /* Alignment of the text inside its bounds */
    private var textAlignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL

    /* Optional path on which to draw the text */
    private var textPath: Path? = null

    /* Stateful text color list */
    private var textColors: ColorStateList? = null

    /* Container for the bounds to be reported to widgets */
    private var textBounds: Rect? = null

    /* Text string to draw */
    private var textToDraw: CharSequence = ""

    /* Attribute lists to pull default values from the current theme */
    private val themeAttributes = intArrayOf(
            android.R.attr.textAppearance
    )
    private val appearanceAttributes = intArrayOf(
            android.R.attr.textSize,
            android.R.attr.typeface,
            android.R.attr.textStyle,
            android.R.attr.textColor
    )

    private fun init(
        context: Context,
        faIconRes: Int,
        @Enums.FontAwesomeIconType.Type iconType: Int,
        @Enums.FontAwesomeVersion.Type faVersion: Int = FontAwesomeVersion.FA_V5
    ) {
        //Used to load and scale resource items
        resources = context.resources

        //Definition of this drawables size
        textBounds = Rect()

        //Paint to use for the text
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint?.density = resources?.displayMetrics?.density
        textPaint?.isDither = true

        var textSize = 15
        var textColor: ColorStateList? = null
        var styleIndex = -1
        var typefaceIndex = -1

        //Set default parameters from the current theme
        val themeArray: TypedArray = context.theme.obtainStyledAttributes(themeAttributes)
        val appearanceId = themeArray.getResourceId(0, -1)
        themeArray.recycle()

        if (appearanceId != -1) {
            val appearanceArray = context.obtainStyledAttributes(appearanceId, appearanceAttributes)
            for (i in 0 until appearanceArray.indexCount) {
                when (val attr = appearanceArray.getIndex(i)) {
                    0 -> textSize = themeArray.getDimensionPixelSize(attr, textSize)
                    1 -> typefaceIndex = themeArray.getInt(attr, typefaceIndex)
                    2 -> styleIndex = themeArray.getInt(attr, styleIndex)
                    3 -> textColor = themeArray.getColorStateList(attr)
                    else -> {
                    }
                }
            }

            appearanceArray.recycle()
        }

        setTextColor(textColor ?: ColorStateList.valueOf(-0x1000000))
        setRawTextSize(textSize.toFloat())
        setTypeface(FontCache[context, iconType, faVersion], styleIndex)

        text = context.getString(faIconRes)
    }

    var text: CharSequence?
        get() = textToDraw
        set(textToDisplay) {
            textToDraw = textToDisplay ?: ""
            measureContent()
        }

    var textSize: Float?
        get() = textPaint?.textSize
        set(size) {
            if (size != null) {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
            }
        }

    private fun setTextSize(unit: Int, size: Float) {
        val dimension = TypedValue.applyDimension(unit, size,
                resources?.displayMetrics)
        setRawTextSize(dimension)
    }

    private fun setRawTextSize(size: Float) {
        if (size != textPaint?.textSize) {
            textPaint?.textSize = size
            measureContent()
        }
    }

    var textScaleX: Float?
        get() = textPaint?.textScaleX
        set(size) {
            size?.let { newSize ->
                if (newSize != textPaint?.textScaleX) {
                    textPaint?.textScaleX = newSize
                    measureContent()
                }
            }
        }

    var textAlign: Layout.Alignment
        get() = textAlignment
        set(align) {
            if (textAlignment !== align) {
                textAlignment = align
                measureContent()
            }
        }

    /**
     * Sets the typeface and style in which the text should be displayed,
     * and turns on the fake bold and italic bits in the Paint if the
     * Typeface that you provided does not have all the bits in the
     * style that you specified.
     */
    private fun setTypeface(tf: Typeface?, style: Int) {
        var newTypeFace = tf

        if (style > 0) {
            newTypeFace = if (newTypeFace == null) {
                Typeface.defaultFromStyle(style)
            } else {
                Typeface.create(newTypeFace, style)
            }
            typeface = newTypeFace
            // now compute what (if any) algorithmic styling is needed
            val typefaceStyle = newTypeFace?.style ?: 0
            val need = style and typefaceStyle.inv()
            textPaint?.isFakeBoldText = need and Typeface.BOLD != 0
            textPaint?.textSkewX = if (need and Typeface.ITALIC != 0) -0.25f else 0F

        } else {
            textPaint?.isFakeBoldText = false
            textPaint?.textSkewX = 0f
            typeface = newTypeFace
        }
    }

    /**
     * Return the current typeface and style that the Paint
     * using for display.
     */
    /**
     * Sets the typeface and style in which the text should be displayed.
     * Note that not all Typeface families actually have bold and italic
     * variants, so you may need to use
     * [.setTypeface] to get the appearance
     * that you actually want.
     */
    private var typeface: Typeface?
        get() = textPaint?.typeface
        set(tf) {
            if (textPaint?.typeface !== tf) {
                textPaint?.typeface = tf
                measureContent()
            }
        }

    /**
     * Set a single text color for all states
     *
     * @param color Color value such as [Color.WHITE] or [Color.argb]
     */
    fun setTextColor(color: Int) {
        setTextColor(ColorStateList.valueOf(color))
    }

    /**
     * Set the text color as a state list
     *
     * @param colorStateList ColorStateList of text colors, such as inflated from an R.color resource
     */
    fun setTextColor(colorStateList: ColorStateList?) {
        textColors = colorStateList
        updateTextColors(state)
    }

    /**
     * Optional Path object on which to draw the text.  If this is set,
     * FontDrawable cannot properly measure the bounds this drawable will need.
     * You must call [setBounds()][.setBounds] before
     * applying this FontDrawable to any View.
     *
     *
     * Calling this method with `null` will remove any Path currently attached.
     */
    fun setTextPath(path: Path) {
        if (textPath !== path) {
            textPath = path
            measureContent()
        }
    }

    /**
     * Internal method to take measurements of the current contents and apply
     * the correct bounds when possible.
     */
    private fun measureContent() {
        //If drawing to a path, we cannot measure intrinsic bounds
        //We must resly on setBounds being called externally
        if (textPath != null) {
            //Clear any previous measurement
            textLayout = null
            textBounds?.setEmpty()

        } else {
            val desired = ceil(Layout.getDesiredWidth(textToDraw, textPaint).toDouble())
            textPaint?.let { textPaint ->
                textLayout = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> StaticLayout.Builder
                            .obtain(textToDraw, 0, textToDraw.length, textPaint, desired.toInt())
                            .setLineSpacing(0.0f, 1.0f)
                            .setIncludePad(false)
                            .build()
                    else -> StaticLayout(text, textPaint, desired.toInt(), textAlignment, 1.0f, 0.0f, false)
                }
            }

            textLayout?.let { textBounds?.set(0, 0, it.width, it.height) }
        }

        //We may need to be redrawn
        invalidateSelf()
    }

    /**
     * Internal method to apply the correct text color based on the drawable's state
     */
    private fun updateTextColors(stateSet: IntArray): Boolean {
        val newColor = textColors!!.getColorForState(stateSet, Color.WHITE)
        if (textPaint!!.color != newColor) {
            textPaint!!.color = newColor
            return true
        }
        return false
    }

    override fun onBoundsChange(bounds: Rect) {
        //Update the internal bounds in response to any external requests
        textBounds?.set(bounds)
    }

    override fun isStateful(): Boolean {
        /*
         * The drawable's ability to represent state is based on
         * the text color list set
         */
        return textColors!!.isStateful
    }

    override fun onStateChange(state: IntArray): Boolean {
        //Upon state changes, grab the correct text color
        return updateTextColors(state)
    }

    override fun getIntrinsicHeight(): Int {
        //Return the vertical bounds measured, or -1 if none
        return textBounds?.let { textBounds -> textBounds.bottom - textBounds.top } ?: -1
    }

    override fun getIntrinsicWidth(): Int {
        //Return the horizontal bounds measured, or -1 if none
        return textBounds?.let { textBounds -> textBounds.right - textBounds.left } ?: -1
    }

    override fun draw(canvas: Canvas) {
        val bounds: Rect = bounds
        val count: Int = canvas.save()

        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
        if (textPath == null) {
            //Allow the layout to draw the text
            textLayout?.draw(canvas)

        } else {
            //Draw directly on the canvas using the supplied path
            textPath?.let { textPath ->
                textPaint?.let { textPaint ->
                    canvas.drawTextOnPath(textToDraw.toString(), textPath, 0F, 0F, textPaint)
                }
            }
        }
        canvas.restoreToCount(count)
    }

    override fun setAlpha(alpha: Int) {
        if (textPaint?.alpha != alpha) {
            textPaint?.alpha = alpha
        }
    }

    override fun getOpacity(): Int {
        return textPaint?.alpha ?: 0
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        if (textPaint?.colorFilter !== colorFilter) {
            textPaint?.colorFilter = colorFilter
        }
    }

    init {
        init(context, faIconRes, iconType)
    }

    fun withColor(color: Int): FontDrawable {
        setTextColor(color)
        return this
    }

    fun withSize(dimen: Int): FontDrawable {
        resources?.getDimension(dimen)?.let { dimension ->
            setRawTextSize(dimension)
        }
        return this
    }
}