package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.ConnectMainViewPagerAdapter
import com.nextiva.nextivaapp.android.constants.Enums.Platform.ViewsToShow.CALLS
import com.nextiva.nextivaapp.android.constants.Enums.Platform.ViewsToShow.CALLS_VOICEMAIL
import com.nextiva.nextivaapp.android.constants.Enums.Platform.ViewsToShow.CHAT
import com.nextiva.nextivaapp.android.constants.Enums.Platform.ViewsToShow.CONTACTS
import com.nextiva.nextivaapp.android.constants.Enums.Platform.ViewsToShow.MEETINGS
import com.nextiva.nextivaapp.android.constants.Enums.Platform.ViewsToShow.MESSAGING
import com.nextiva.nextivaapp.android.constants.Enums.Platform.ViewsToShow.MORE
import com.nextiva.nextivaapp.android.constants.Enums.Platform.ViewsToShow.ROOMS
import com.nextiva.nextivaapp.android.features.messaging.view.MessageListFragment
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsListFragment
import com.nextiva.nextivaapp.android.features.rooms.view.TeamChatListFragment
import com.nextiva.nextivaapp.android.fragments.ConnectCallsFragment
import com.nextiva.nextivaapp.android.fragments.ConnectContactsListFragment
import com.nextiva.nextivaapp.android.fragments.ConnectMeetingsListFragment
import com.nextiva.nextivaapp.android.fragments.GeneralRecyclerViewFragment
import com.nextiva.nextivaapp.android.models.BottomNavigationItem
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.UIUtil.Companion.getFontAwesomeDrawable

class ConnectBottomNavigationView : BottomNavigationView {
    var isSmsEnabled: Boolean = false
    var isMeetingEnabled: Boolean = false
    var isRoomsEnabled: Boolean = false

    constructor(context: Context) : super(context) {
        setIcons()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setIcons()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setIcons()
    }

    private val defaultBottomNavigationItems = listOf(
        BottomNavigationItem(
            ConnectMainViewPagerAdapter.FeatureType.Calls,
            context.getString(R.string.connect_main_bottom_navigation_calls),
            faIcon = R.string.fa_phone_alt,
            fragment = getFragment(ConnectMainViewPagerAdapter.FeatureType.Calls, {}, ConnectCallsFragment.CallTabs.ALL_TAB_ID.id, {})
        ),
        BottomNavigationItem(
            ConnectMainViewPagerAdapter.FeatureType.Messaging,
            context.getString(R.string.connect_main_bottom_navigation_messaging),
            faIcon = R.string.fa_comment_dots,
            fragment = getFragment(ConnectMainViewPagerAdapter.FeatureType.Messaging)
        ),
        BottomNavigationItem(
            ConnectMainViewPagerAdapter.FeatureType.Contacts,
            context.getString(R.string.connect_main_bottom_navigation_contacts),
            faIcon = R.string.fa_user_circle,
            fragment = getFragment(ConnectMainViewPagerAdapter.FeatureType.Contacts)
        ),
        BottomNavigationItem(
            ConnectMainViewPagerAdapter.FeatureType.Chat,
            context.getString(R.string.connect_main_bottom_navigation_chat),
            faIcon = R.string.fa_comments_alt,
            fragment = getFragment(ConnectMainViewPagerAdapter.FeatureType.Chat)
        ),
        BottomNavigationItem(
            ConnectMainViewPagerAdapter.FeatureType.Rooms,
            context.getString(R.string.connect_main_bottom_navigation_rooms),
            faIcon = R.string.fa_door_open,
            fragment = getFragment(ConnectMainViewPagerAdapter.FeatureType.Rooms)
        ),
        /*BottomNavigationItem(
            ConnectMainViewPagerAdapter.FeatureType.Meetings,
            context.getString(R.string.connect_main_bottom_navigation_meetings),
            faIcon = R.string.fa_video,
            fragment = getFragment(ConnectMainViewPagerAdapter.FeatureType.Meetings)
        ),
        BottomNavigationItem(
            ConnectMainViewPagerAdapter.FeatureType.Voicemail,
            context.getString(R.string.connect_main_bottom_navigation_voicemail),
            faIcon = R.string.fa_voicemail,
            fragment = getFragment(ConnectMainViewPagerAdapter.FeatureType.Voicemail, {}, ConnectCallsFragment.CallTabs.VOICEMAIL_TAB_ID.id)
        ),
        BottomNavigationItem(
            ConnectMainViewPagerAdapter.FeatureType.Calendar,
            context.getString(R.string.connect_main_bottom_navigation_calendar),
            faIcon = R.string.fa_calendar,
            fragment = getFragment(ConnectMainViewPagerAdapter.FeatureType.Calendar)
        )*/
    )

    fun getBottomNavigationItems(): List<BottomNavigationItem> {
            val listItems = ArrayList<BottomNavigationItem>(defaultBottomNavigationItems)

            if (!isRoomsEnabled) {
                listItems.removeIf {
                    it.itemId == ConnectMainViewPagerAdapter.FeatureType.Rooms ||
                            it.itemId == ConnectMainViewPagerAdapter.FeatureType.Chat
                }
            }

            if (!isSmsEnabled) {
                listItems.removeIf {
                    it.itemId == ConnectMainViewPagerAdapter.FeatureType.Messaging
                }
            }

            if (!isMeetingEnabled) {
                listItems.removeIf {
                    it.itemId == ConnectMainViewPagerAdapter.FeatureType.Meetings
                }
            }
        return listItems
    }

    fun setFeatureFlags(isSmsEnabled: Boolean, isMeetingsEnable: Boolean, isRoomsEnabled: Boolean) {
        this.isSmsEnabled = isSmsEnabled
        this.isMeetingEnabled = isMeetingsEnable
        this.isRoomsEnabled = isRoomsEnabled
    }

    private fun getFragment(featureType: ConnectMainViewPagerAdapter.FeatureType): GeneralRecyclerViewFragment?
    {
        return getFragment(featureType, {}, null, {})
    }
    private fun getFragment(featureType: ConnectMainViewPagerAdapter.FeatureType, searchViewFocusChangeCallback: (Boolean) -> Unit, startTab: Int?, editViewModeChangeCallback: (Boolean) -> Unit): GeneralRecyclerViewFragment? {
        LogUtil.d("ConnectMainViewPagerAdapter", "Navigation Getting fragment for ${featureType.name}")
        return when (featureType) {
            ConnectMainViewPagerAdapter.FeatureType.Calls ->
                ConnectCallsFragment(
                    searchViewFocusChangeCallback,
                    getCallTab(startTab ?: ConnectCallsFragment.CallTabs.ALL_TAB_ID.id)
                )

            ConnectMainViewPagerAdapter.FeatureType.Voicemail ->
                ConnectCallsFragment(
                    searchViewFocusChangeCallback,
                    ConnectCallsFragment.CallTabs.VOICEMAIL_TAB_ID
                )

            ConnectMainViewPagerAdapter.FeatureType.Contacts ->
                ConnectContactsListFragment(
                    searchViewFocusChangeCallback
                )

            ConnectMainViewPagerAdapter.FeatureType.Messaging -> if (isSmsEnabled)
                MessageListFragment(searchViewFocusChangeCallback) else null

            ConnectMainViewPagerAdapter.FeatureType.Meetings -> {
                if (isMeetingEnabled)
                    ConnectMeetingsListFragment(searchViewFocusChangeCallback) else null
            }

            ConnectMainViewPagerAdapter.FeatureType.Rooms -> if (isRoomsEnabled)
                ConnectRoomsListFragment(searchViewFocusChangeCallback) else null

            ConnectMainViewPagerAdapter.FeatureType.Chat -> if (isRoomsEnabled)
                TeamChatListFragment(
                    searchViewFocusChangeCallback
                ) else null
            else -> {
                null
            }
        }
    }

    fun getCallTab(startTab: Int): ConnectCallsFragment.CallTabs
    {
        return when (startTab) {
            ConnectCallsFragment.CallTabs.ALL_TAB_ID.id -> {
                ConnectCallsFragment.CallTabs.ALL_TAB_ID
            }
            ConnectCallsFragment.CallTabs.VOICEMAIL_TAB_ID.id -> {
                ConnectCallsFragment.CallTabs.VOICEMAIL_TAB_ID
            }
            ConnectCallsFragment.CallTabs.MISSED_TAB_ID.id -> {
                ConnectCallsFragment.CallTabs.MISSED_TAB_ID
            }
            else -> {
                ConnectCallsFragment.CallTabs.ALL_TAB_ID
            }
        }
    }

    fun setIcons() {
        setIcons(null, getBottomNavigationItems())
    }

    fun setIcons(newSelectedId: Int?) {
        setIcons(newSelectedId, getBottomNavigationItems())
    }

    fun setIcons(newSelectedId: Int?, navListItems: List<BottomNavigationItem>) {
        menu.children.forEach { navItem ->
            (navItem as? MenuItem)?.let { itemView ->
                val isItemSelected = (newSelectedId ?: selectedItemId) == itemView.itemId

                if (itemView.title == context.getString(R.string.connect_main_bottom_navigation_more)) {
                    itemView.icon = ContextCompat.getDrawable(this.context, R.drawable.more_icon)

                } else {
                    LogUtil.d("Navigation ConnectBottomNavigationView", "Navigation Setting icon for ${itemView.title}")
                    itemView.icon = when (itemView.itemId) {
                        R.id.connect_main_bottom_navigation_item_1 -> getNavListItemDrawable(navListItems[0], isItemSelected)

                        R.id.connect_main_bottom_navigation_item_2 -> getNavListItemDrawable(navListItems[1], isItemSelected)

                        R.id.connect_main_bottom_navigation_item_3 -> getNavListItemDrawable(navListItems[2], isItemSelected)

                        R.id.connect_main_bottom_navigation_item_4 -> getNavListItemDrawable(navListItems[3], isItemSelected)

                        R.id.connect_main_bottom_navigation_item_5 -> getNavListItemDrawable(navListItems[4], isItemSelected)

                        else -> null
                    }
                }
            }
        }
    }

    private fun getNavListItemDrawable(navItem: BottomNavigationItem, isItemSelected: Boolean): Drawable? {
        return if(navItem.faIcon != null) navItem.faIcon?.let {
            getFontAwesomeDrawable(
                isItemSelected,
                it
            )
        } else navItem.drawableIcon?.let {
            ContextCompat.getDrawable(this.context, it)
        }
    }

    @Deprecated("Use Icons on Nav List Item instead")
    private fun getIconForNavigationItem(navItem: ConnectMainViewPagerAdapter.FeatureType): Drawable? {
        return when (navItem) {
            ConnectMainViewPagerAdapter.FeatureType.Calls -> getFontAwesomeDrawable(R.string.fa_phone_alt)
            ConnectMainViewPagerAdapter.FeatureType.Voicemail -> getFontAwesomeDrawable(R.string.fa_comment_dots)
            ConnectMainViewPagerAdapter.FeatureType.Messaging -> getFontAwesomeDrawable(R.string.fa_video)
            ConnectMainViewPagerAdapter.FeatureType.Meetings -> getFontAwesomeDrawable(R.string.fa_user)
            ConnectMainViewPagerAdapter.FeatureType.Contacts -> getFontAwesomeDrawable(R.string.fa_user_circle)
            ConnectMainViewPagerAdapter.FeatureType.Rooms -> getFontAwesomeDrawable(R.string.fa_door_open)
            ConnectMainViewPagerAdapter.FeatureType.Chat -> getFontAwesomeDrawable(R.string.fa_comments_alt)
            ConnectMainViewPagerAdapter.FeatureType.More -> getFontAwesomeDrawable(R.string.fa_grid_2)
            ConnectMainViewPagerAdapter.FeatureType.Calendar -> getFontAwesomeDrawable(R.string.fa_calendar)
            else -> null
        }
    }

    fun getFeatureTypeFromViewToShow(viewToShow: Int): ConnectMainViewPagerAdapter.FeatureType {
        return when (viewToShow) {
            CALLS -> ConnectMainViewPagerAdapter.FeatureType.Calls
            CALLS_VOICEMAIL -> ConnectMainViewPagerAdapter.FeatureType.Voicemail
            CONTACTS -> ConnectMainViewPagerAdapter.FeatureType.Contacts
            MESSAGING -> ConnectMainViewPagerAdapter.FeatureType.Messaging
            MEETINGS -> ConnectMainViewPagerAdapter.FeatureType.Meetings
            ROOMS -> ConnectMainViewPagerAdapter.FeatureType.Rooms
            CHAT -> ConnectMainViewPagerAdapter.FeatureType.Chat
            MORE -> ConnectMainViewPagerAdapter.FeatureType.More
            else -> ConnectMainViewPagerAdapter.FeatureType.More
        }
    }

    private fun getFontAwesomeDrawable(iconId: Int): Drawable {
        return getFontAwesomeDrawable(context, false, iconId)
    }

    private fun getFontAwesomeDrawable(isSelected: Boolean, iconId: Int): Drawable {
        return getFontAwesomeDrawable(context, isSelected, iconId)
    }

    fun setItemSelected(itemId: Int) {
        if (itemId != -1) {
            val menuItem = menu.findItem(itemId)
            if (menuItem != null)
                menuItem.isChecked = true
            setIcons()
        }
    }

    fun setBadge(itemId: Int, newCount: Int) {
        if (newCount > 0) {
            val badge = getOrCreateBadge(itemId)
            badge.verticalOffset = getVerticalOffset()
            badge.maxCharacterCount = 3
            badge.number = newCount
            badge.backgroundColor = ContextCompat.getColor(context, R.color.connectPrimaryRed)
            badge.badgeTextColor = ContextCompat.getColor(context, R.color.connectWhite)
            badge.badgeGravity = BadgeDrawable.BOTTOM_END

        } else {
            removeBadge(itemId)
        }
    }

    private fun getVerticalOffset(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            resources.getDimension(R.dimen.general_padding_xxsmall),
            resources.displayMetrics
        ).toInt()
    }

}