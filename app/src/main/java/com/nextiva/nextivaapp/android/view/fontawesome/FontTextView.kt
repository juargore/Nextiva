package com.nextiva.nextivaapp.android.view.fontawesome

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.FontAwesomeVersion
import com.nextiva.nextivaapp.android.util.fontawesome.FontCache

open class FontTextView : AppCompatTextView {
    @Enums.FontAwesomeIconType.Type
    var iconType: Int = Enums.FontAwesomeIconType.REGULAR
    private var faVersion = FontAwesomeVersion.FA_V5

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.FontTextView,
                defStyle, 0)
        if (attributes.getBoolean(R.styleable.FontTextView_solid_icon, false)) {
            iconType = Enums.FontAwesomeIconType.SOLID
        }

        if (attributes.getBoolean(R.styleable.FontTextView_custom_icon, false)) {
            iconType = Enums.FontAwesomeIconType.CUSTOM
        }

        if (attributes.getBoolean(R.styleable.FontTextView_brand_icon, false)) {
            iconType = Enums.FontAwesomeIconType.BRAND
        }

        if (attributes.getString(R.styleable.FontTextView_fa_version) == context.getString(R.string.fa_version_v6)){
            faVersion = FontAwesomeVersion.FA_V6
        }

        typeface = FontCache[context, iconType, faVersion]
        firstBaselineToTopHeight = 0
        includeFontPadding = false
        setIcon(attributes.getString(R.styleable.FontTextView_icon_string), iconType)
    }

    fun setIcon(iconString: String?, iconType: Int) {
        iconString?.let { icon ->
            typeface = FontCache[context, iconType, faVersion]
            text = icon
        }
    }

    fun setIcon(id: Int, iconType: Int) {
        typeface = FontCache[context, iconType, faVersion]
        text = context.getString(id)
    }
}