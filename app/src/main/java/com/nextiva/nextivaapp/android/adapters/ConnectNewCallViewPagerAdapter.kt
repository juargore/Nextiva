package com.nextiva.nextivaapp.android.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nextiva.nextivaapp.android.fragments.ConnectCallsFragment
import com.nextiva.nextivaapp.android.fragments.ConnectContactsListFragment

class ConnectNewCallViewPagerAdapter(fragmentActivity: FragmentActivity, searchViewFocusChangeCallback: (Boolean) -> Unit) : FragmentStateAdapter(fragmentActivity) {

    var tabCount = 2

    var callsTabIndex = 0
    var contactsTabIndex = 1

    private val connectCallsFragment = ConnectCallsFragment(searchViewFocusChangeCallback)
    private val connectContactsFragment = ConnectContactsListFragment(searchViewFocusChangeCallback)

    override fun getItemCount(): Int {
        return tabCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            callsTabIndex -> connectCallsFragment
            else -> connectContactsFragment
        }
    }
}