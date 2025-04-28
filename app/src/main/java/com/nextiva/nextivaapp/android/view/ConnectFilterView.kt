package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R

class ConnectFilterView: LinearLayout {
    private lateinit var title: TextView

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_connect_filter, this, true)
        title = findViewById(R.id.connect_filter_name)

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ConnectFilterView, 0, 0)
            title.text = ta.getString(R.styleable.ConnectFilterView_defaultText)

            ta.recycle()
        }
    }

    fun setSelectedOption(option: String) {
        setSelectedOption(option, null)
    }

    fun setSelectedOption(option: String, colorId: Int?) {
        title.text = option
        title.setTextColor(ContextCompat.getColor(context, colorId ?: R.color.connectSecondaryDarkBlue))
    }

    fun getSelectedOption() : String = title.text.toString()
}