package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

class ConnectEmptyStateView: LinearLayout {

    private lateinit var image: ImageView
    private lateinit var icon: FontTextView
    private lateinit var title: TextView
    private lateinit var message: TextView
    private lateinit var textButton: TextView

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
        inflater.inflate(R.layout.view_connect_empty_state, this, true)
        image = findViewById(R.id.connect_empty_state_image)
        title = findViewById(R.id.connect_empty_state_view_title)
        message = findViewById(R.id.connect_empty_state_view_message)
        icon = findViewById(R.id.connect_empty_state_view_icon)
        textButton = findViewById(R.id.connect_empty_state_view_button)

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ConnectEmptyStateView, 0, 0)
            title.text = ta.getString(R.styleable.ConnectEmptyStateView_emptyStateTitle)
            message.text = ta.getString(R.styleable.ConnectEmptyStateView_emptyStateMessage)
            icon.setIcon(ta.getString(R.styleable.ConnectEmptyStateView_emptyStateIcon), Enums.FontAwesomeIconType.REGULAR)
            textButton.text = ta.getString(R.styleable.ConnectEmptyStateView_emptyStateButtonText)

            if (ta.getResourceId(R.styleable.ConnectEmptyStateView_emptyStateImage, -1) != -1) {
                image.visibility = View.VISIBLE
                image.setImageDrawable(ContextCompat.getDrawable(context, ta.getResourceId(R.styleable.ConnectEmptyStateView_emptyStateImage, -1)))
                icon.visibility = View.GONE
            } else {
                image.visibility = View.GONE
                icon.visibility = View.VISIBLE
            }

            if (textButton.text.isNullOrEmpty()) {
                textButton.visibility = View.GONE
            } else {
                textButton.visibility = View.VISIBLE
            }

            ta.recycle()
        }
    }

    fun setButtonClickListener(clickListener: OnClickListener) {
        textButton.setOnClickListener(clickListener)
    }
}