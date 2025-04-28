package com.nextiva.nextivaapp.android.features.rooms.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.view.AvatarView

class RoomDetailHeaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private lateinit var avatarView: AvatarView
    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
    lateinit var showProfileTextView: TextView

    init {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_connect_room_header, this, true)
        avatarView = findViewById(R.id.connect_contact_header_avatar_view)
        nameTextView = findViewById(R.id.connect_contact_header_name_text_view)
        descriptionTextView = findViewById(R.id.connect_contact_header_description_text_view)
        showProfileTextView = findViewById(R.id.connect_contact_header_show_full_profile_text_view)
    }

    fun setAvatar(avatarInfo: AvatarInfo) {
        avatarInfo.size = AvatarInfo.SIZE_LARGE
        avatarView.setAvatar(avatarInfo)
    }

    fun setNameText(name: String) {
        nameTextView.text = name
    }

    fun setDescriptionText(description: String) {
        descriptionTextView.text = description
    }

}