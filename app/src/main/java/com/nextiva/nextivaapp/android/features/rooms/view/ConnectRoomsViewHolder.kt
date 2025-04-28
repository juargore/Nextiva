package com.nextiva.nextivaapp.android.features.rooms.view

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.switchMap
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.BaseViewHolder
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemConnectContactBinding
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.RoomsMasterListListener
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.view.AvatarView
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import java.util.regex.Matcher
import java.util.regex.Pattern

internal class ConnectRoomsViewHolder(
    itemView: View,
    context: Context,
    masterListListener: MasterListListener,
    private val sessionManager: SessionManager,
    private val roomsDbManager: RoomsDbManager
): BaseViewHolder<ConnectRoomsListItem>(itemView, context, masterListListener), View.OnClickListener, View.OnLongClickListener {

    private val masterItemView: View

    private lateinit var avatarView: AvatarView
    private lateinit var name: TextView
    private lateinit var sharedIcon: FontTextView
    private lateinit var favoriteIcon: FontTextView
    private lateinit var statusText: TextView
    private lateinit var searchMatch: TextView
    private lateinit var countText: AppCompatTextView

    private val unreadMessageObserver = Observer<Int?>{
        setUnreadMessageCountUI(it)
    }

    private var roomId: MutableLiveData<String?> = MutableLiveData(null)

    private var countLiveData: LiveData<Int?> = roomId.switchMap {
        return@switchMap roomsDbManager.getUnreadMessageByRoomIdLiveData(it ?: "")
    }

    constructor(parent: ViewGroup, context: Context, masterListListener: RoomsMasterListListener, sessionManager: SessionManager, roomsDbManager: RoomsDbManager) :
            this(inflateView(parent), context, masterListListener, sessionManager, roomsDbManager)

    companion object {
        internal fun inflateView(parent: ViewGroup): View {
            return LayoutInflater.from(parent.context).inflate(R.layout.list_item_connect_contact, parent, false)
        }
    }

    init {
        bindViews(itemView)
        masterItemView = itemView
        masterItemView.setOnClickListener(this)
        masterItemView.setOnLongClickListener(this)
        favoriteIcon.setOnClickListener(this)
    }

    override fun bind(listItem: ConnectRoomsListItem) {
        removeItemViewFromParent()

        mListItem = listItem
        this.roomId.postValue(listItem.room.roomId)

        if (listItem.room.typeEnum() == RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM) {
            var myRoom = mContext.getString(R.string.my_status_my_room)
            var displayName = myRoom
            sessionManager.userDetails?.fullName?.let {
                if (!TextUtils.isEmpty(it)) {
                    myRoom = "$myRoom ($it)"
                    displayName = it
                }
            }
            name.text = myRoom

            avatarView.setAvatar(AvatarInfo.Builder()
                .isConnect(true)
                .setDisplayName(displayName)
                .setFontAwesomeIconResId(R.string.fa_user)
                .build())
        } else {
            name.text = listItem.room.name

            avatarView.setAvatar(AvatarInfo.Builder()
                .isConnect(true)
                .setDisplayName(listItem.room.name)
                .setFontAwesomeIconResId(R.string.fa_door_open)
                .setFontAwesomeFontResId(R.font.fa_solid_900)
                .setAlwaysShowIcon(true)
                .build())
        }

        if (listItem.room.type?.equals(RoomsEnums.ConnectRoomsTypes.PRIVATE_ROOM.value) == true) {
            sharedIcon.visibility = View.VISIBLE
            sharedIcon.setIcon(R.string.fa_lock, Enums.FontAwesomeIconType.SOLID)
        } else {
            sharedIcon.visibility = View.GONE
        }

        setUnreadMessageCountUI(listItem.room.unreadMessageCount)

        setFavoriteUI(listItem.room.requestorFavorite?.or(false) == true)
        setContentDescriptions()
    }

    private fun setFavoriteUI(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteIcon.setIcon(R.string.fa_star, Enums.FontAwesomeIconType.SOLID)
            favoriteIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectPrimaryYellow))
        } else {
            favoriteIcon.setIcon(R.string.fa_star, Enums.FontAwesomeIconType.REGULAR)
            favoriteIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectGrey08))
        }
    }

    fun getText(input: String, searchTerm: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder(input)
        val pattern: Pattern = Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(input)

        while (matcher.find()) {
            builder.setSpan(BackgroundColorSpan(ContextCompat.getColor(mContext, R.color.connectSecondaryYellow)),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }

        return builder
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectContactBinding.bind(view)

        avatarView = binding.listItemConnectContactAvatarView
        name = binding.listItemConnectContactName
        sharedIcon = binding.listItemConnectContactSharedIcon
        favoriteIcon = binding.listItemConnectContactFavoriteIcon
        searchMatch = binding.listItemConnectContactSearchMatch
        statusText = binding.listItemConnectContactPresenceStatus
        countText = binding.listItemConnectContactCount
    }

    private fun setContentDescriptions() {
        masterItemView.contentDescription = mContext.getString(R.string.connect_contact_list_item_content_description, name.text)
        name.contentDescription = mContext.getString(R.string.connect_contact_list_item_name_content_description, name.text)
        avatarView.contentDescription = mContext.getString(R.string.connect_contact_list_item_avatar_content_description, name.text)
        sharedIcon.contentDescription = mContext.getString(R.string.connect_contact_list_item_shared_icon_content_description, name.text)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            favoriteIcon.id -> {
                (mMasterListListener as RoomsMasterListListener)?.onConnectRoomFavoriteIconClicked(mListItem)
                (mListItem.room?.requestorFavorite)?.let { isFavorite ->
                    setFavoriteUI(!isFavorite)
                    setContentDescriptions()
                }
            }
            else -> {
                (mMasterListListener as RoomsMasterListListener)?.onConnectRoomListItemClicked(mListItem)
            }
        }
    }

    fun observeLiveData() {
        countLiveData.observeForever(unreadMessageObserver)
    }

    fun removeObservers() {
        countLiveData.removeObserver(unreadMessageObserver)
    }

    override fun onLongClick(view: View?): Boolean {
        (mMasterListListener as RoomsMasterListListener)?.onConnectRoomListItemLongClicked(mListItem)
        return true
    }

    private fun setUnreadMessageCountUI(count: Int?){
        if(count != null && count > 0 ){
            countText.visibility = View.VISIBLE
            countText.text = count.toString()
            name.setTextColor(ContextCompat.getColor(mContext, R.color.connectPrimaryBlue))
            name.setTypeface(null, Typeface.BOLD)
        } else {
            countText.visibility = View.GONE
            name.setTextColor(ContextCompat.getColor(mContext, R.color.connectSecondaryDarkBlue))
            name.setTypeface(null, Typeface.NORMAL)
        }
    }
}