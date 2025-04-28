package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

class ConnectPresenceFilterView: LinearLayout {
    enum class PresenceState { AUTOMATIC, AVAILABLE, BUSY, AWAY, OFFLINE, DND }

    private lateinit var title: TextView
    private lateinit var icon: FontTextView
    private lateinit var stroke: FontTextView
    private lateinit var parent: ConstraintLayout
    private lateinit var arrow: FontTextView

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
        inflater.inflate(R.layout.view_connect_presence_filter, this, true)
        title = findViewById(R.id.connect_presence_filter_name)
        icon = findViewById(R.id.connect_presence_filter_icon)
        stroke = findViewById(R.id.connect_presence_filter_icon_stroke)
        arrow = findViewById(R.id.connect_presence_filter_arrow)
        parent = findViewById(R.id.connect_presence_filter_parent)

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ConnectPresenceFilterView, 0, 0)
            ta.recycle()
        }
    }

    private fun setPresence(presenceState: PresenceState, status: String) {
        icon.setIcon(R.string.fa_circle, Enums.FontAwesomeIconType.SOLID)
        arrow.bringToFront()
        when (presenceState) {
            PresenceState.AVAILABLE -> {
                title.text = status
                icon.visibility = View.VISIBLE
                stroke.visibility = View.GONE
                icon.setIcon(R.string.fa_check_circle, Enums.FontAwesomeIconType.SOLID)
                icon.setTextColor(ContextCompat.getColor(context, R.color.connectPrimaryGreen))
                val drawable = parent.background as GradientDrawable
                drawable.setStroke(4, ContextCompat.getColor(context, R.color.connectPrimaryGreen))
            }
            PresenceState.AWAY -> {
                title.text = status
                icon.visibility = View.VISIBLE
                stroke.visibility = View.GONE
                icon.setIcon(R.string.fa_clock, Enums.FontAwesomeIconType.SOLID)
                icon.setTextColor(ContextCompat.getColor(context, R.color.connectPrimaryYellow))
                val drawable = parent.background as GradientDrawable
                drawable.setStroke(4, ContextCompat.getColor(context, R.color.connectPrimaryYellow))
            }
            PresenceState.BUSY -> {
                title.text = status
                icon.visibility = View.VISIBLE
                stroke.visibility = View.GONE
                icon.setTextColor(ContextCompat.getColor(context, R.color.connectPrimaryRed))
                val drawable = parent.background as GradientDrawable
                drawable.setStroke(4, ContextCompat.getColor(context, R.color.connectPrimaryRed))
            }
            PresenceState.DND -> {
                title.text = status
                icon.visibility = View.VISIBLE
                stroke.visibility = View.GONE
                icon.setIcon(R.string.fa_do_not_disturb, Enums.FontAwesomeIconType.SOLID)
                icon.setTextColor(ContextCompat.getColor(context, R.color.connectPrimaryRed))
                val drawable = parent.background as GradientDrawable
                drawable.setStroke(4, ContextCompat.getColor(context, R.color.connectPrimaryRed))
            }
            PresenceState.OFFLINE -> {
                title.text = status
                icon.visibility = View.VISIBLE
                stroke.visibility = View.VISIBLE
                icon.setTextColor(ContextCompat.getColor(context, R.color.avatarConnectOfflinePresenceFill))
                val drawable = parent.background as GradientDrawable
                drawable.setStroke(4, ContextCompat.getColor(context, R.color.avatarConnectOfflinePresenceStroke))
            }
            PresenceState.AUTOMATIC -> {
                // ignored
            }
        }
    }

    fun setPresence(presence: DbPresence?) {
        val state = presence?.state ?: PresenceState.OFFLINE
        val text = presence?.humanReadablePresenceText ?: ""
        setPresence(when (state) {
            Enums.Contacts.PresenceStates.CONNECT_ACTIVE,
            Enums.Contacts.PresenceStates.CONNECT_ONLINE -> PresenceState.AVAILABLE
            Enums.Contacts.PresenceStates.CONNECT_BE_RIGHT_BACK,
            Enums.Contacts.PresenceStates.CONNECT_AWAY -> PresenceState.AWAY
            Enums.Contacts.PresenceStates.CONNECT_OUT_OF_OFFICE,
            Enums.Contacts.PresenceStates.CONNECT_DND -> PresenceState.DND
            Enums.Contacts.PresenceStates.CONNECT_BUSY -> PresenceState.BUSY
            Enums.Contacts.PresenceStates.CONNECT_OFFLINE,
            Enums.Contacts.PresenceStates.CONNECT_AUTOMATIC -> PresenceState.OFFLINE
            else -> PresenceState.OFFLINE
        }, text)
    }
}
