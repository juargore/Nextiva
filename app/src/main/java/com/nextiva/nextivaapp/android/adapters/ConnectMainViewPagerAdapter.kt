package com.nextiva.nextivaapp.android.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Platform.ViewsToShow.NO_VIEW
import com.nextiva.nextivaapp.android.features.messaging.view.MessageListFragment
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsListFragment
import com.nextiva.nextivaapp.android.features.rooms.view.TeamChatListFragment
import com.nextiva.nextivaapp.android.fragments.ConnectCallsFragment
import com.nextiva.nextivaapp.android.fragments.ConnectContactsListFragment
import com.nextiva.nextivaapp.android.fragments.ConnectMeetingsListFragment
import com.nextiva.nextivaapp.android.fragments.GeneralRecyclerViewFragment
import com.nextiva.nextivaapp.android.models.BottomNavigationItem

class ConnectMainViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    val isSmsEnabled: Boolean,
    val isMeetingEnabled: Boolean,
    val isRoomsEnabled: Boolean,
    var bottomNavigationItemList: List<BottomNavigationItem>,
    var navBarCount: Int,
    val viewToShow: Int,
    searchViewFocusChangeCallback: (Boolean) -> Unit
) : FragmentStateAdapter(fragmentActivity) {

    enum class FeatureType {
        Calls, Voicemail, Messaging, Meetings, Contacts, Rooms, Chat, Calendar, More, Unknown
    }

    private val fragments = ArrayList<GeneralRecyclerViewFragment>().apply {

        for ((count, item) in bottomNavigationItemList.withIndex())
        {
            if (bottomNavigationItemList.size <= 5 || count < 4) {
                when (item.itemId) {
                    FeatureType.Calls -> add(ConnectCallsFragment(searchViewFocusChangeCallback, getStartingCallsTab()))
                    FeatureType.Voicemail -> add(ConnectCallsFragment(searchViewFocusChangeCallback, ConnectCallsFragment.CallTabs.VOICEMAIL_TAB_ID))
                    FeatureType.Contacts -> add(ConnectContactsListFragment(searchViewFocusChangeCallback))
                    FeatureType.Messaging -> if (isSmsEnabled) add(MessageListFragment(searchViewFocusChangeCallback))
                    FeatureType.Meetings -> if (isMeetingEnabled) add(ConnectMeetingsListFragment(searchViewFocusChangeCallback))
                    FeatureType.Rooms -> if (isRoomsEnabled) add(ConnectRoomsListFragment(searchViewFocusChangeCallback))
                    FeatureType.Chat -> if(isRoomsEnabled) add(TeamChatListFragment(searchViewFocusChangeCallback))
                    else -> {}
                }
            }
            else
            {
                if(viewToShow != NO_VIEW) {
                    add(getMoreFragment(searchViewFocusChangeCallback, viewToShow))
                }
                break
            }
        }
    }

    private fun getMoreFragment(searchViewFocusChangeCallback: (Boolean) -> Unit, viewToShow: Int): GeneralRecyclerViewFragment {
        when (viewToShow)
        {
            Enums.Platform.ViewsToShow.CALLS -> return ConnectCallsFragment(searchViewFocusChangeCallback, ConnectCallsFragment.CallTabs.ALL_TAB_ID) //The else will likely change to whatever is in the first slot so don't remove this until you're ready to fix that
            Enums.Platform.ViewsToShow.CALLS_VOICEMAIL-> return ConnectCallsFragment(searchViewFocusChangeCallback, ConnectCallsFragment.CallTabs.VOICEMAIL_TAB_ID)
            Enums.Platform.ViewsToShow.CALLS_MISSED -> return ConnectCallsFragment(searchViewFocusChangeCallback, ConnectCallsFragment.CallTabs.MISSED_TAB_ID)
            Enums.Platform.ViewsToShow.CONTACTS -> return ConnectContactsListFragment(searchViewFocusChangeCallback)
            Enums.Platform.ViewsToShow.MESSAGING -> return MessageListFragment(searchViewFocusChangeCallback)
            Enums.Platform.ViewsToShow.MEETINGS -> return ConnectMeetingsListFragment(searchViewFocusChangeCallback)
            Enums.Platform.ViewsToShow.ROOMS -> return ConnectRoomsListFragment(searchViewFocusChangeCallback)
            Enums.Platform.ViewsToShow.CHAT -> return TeamChatListFragment(searchViewFocusChangeCallback)
            else -> {ConnectCallsFragment(searchViewFocusChangeCallback, ConnectCallsFragment.CallTabs.ALL_TAB_ID)}
        }

        return ConnectMeetingsListFragment(searchViewFocusChangeCallback)
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun scrollToTop(currentIndex: Int) {
        fragments[currentIndex].scrollToTop()
    }

    fun getFragment(index: Int): Fragment? {
        return if (index < fragments.size) {
            return fragments[index]
        } else
            null
    }

    fun getFeatureType(position: Int): FeatureType = when (fragments[position]) {
        is ConnectCallsFragment -> FeatureType.Calls
        //is ConnectCallsFragment -> FeatureType.Voicemail
        is MessageListFragment -> FeatureType.Messaging
        is ConnectMeetingsListFragment -> FeatureType.Meetings
        is ConnectContactsListFragment -> FeatureType.Contacts
        is ConnectRoomsListFragment -> FeatureType.Rooms
        is TeamChatListFragment -> FeatureType.Chat
        //is ConnectMeetingsListFragment -> FeatureType.Calendar
        else -> FeatureType.Unknown
    }

    private fun getStartingCallsTab(): ConnectCallsFragment.CallTabs {
        return when(viewToShow) {
            Enums.Platform.ViewsToShow.CALLS -> return ConnectCallsFragment.CallTabs.ALL_TAB_ID
            Enums.Platform.ViewsToShow.CALLS_MISSED -> return ConnectCallsFragment.CallTabs.MISSED_TAB_ID
            Enums.Platform.ViewsToShow.CALLS_VOICEMAIL -> return ConnectCallsFragment.CallTabs.VOICEMAIL_TAB_ID
            else -> { ConnectCallsFragment.CallTabs.ALL_TAB_ID}
        }
    }
}