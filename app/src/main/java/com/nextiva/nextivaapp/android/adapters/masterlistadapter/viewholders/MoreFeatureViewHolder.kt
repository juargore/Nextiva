package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.ConnectMainActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.BottomSheetBottomNavigationMoreAdapter
import com.nextiva.nextivaapp.android.adapters.ConnectMainViewPagerAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemBottomSheetBottomNavigationMoreItemBinding
import com.nextiva.nextivaapp.android.models.BottomNavigationItem
import com.nextiva.nextivaapp.android.util.UIUtil


class MoreFeatureViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private lateinit var context: Context
    lateinit var iconView: AppCompatImageView
    lateinit var titleTextView: TextView
    lateinit var listItemBottomSheetBottomNavigationMoreLayout: ConstraintLayout
    lateinit var countTextView: AppCompatTextView
    private lateinit var mUnreadCountLiveData: BottomSheetBottomNavigationMoreAdapter.UnreadCountLiveData

    constructor(
        parent: ViewGroup,
        unreadCountLiveData: BottomSheetBottomNavigationMoreAdapter.UnreadCountLiveData
    ) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_bottom_sheet_bottom_navigation_more_item, parent, false)
    ) {
        context = parent.context
        mUnreadCountLiveData = unreadCountLiveData
    }

    init {
        bindViews(itemView)
    }

    private fun bindViews(view: View) {
        val binding = ListItemBottomSheetBottomNavigationMoreItemBinding.bind(view)
        iconView = binding.listItemBottomSheetBottomNavigationMoreIcon
        titleTextView = binding.listItemBottomSheetBottomNavigationMoreTitle
        listItemBottomSheetBottomNavigationMoreLayout = binding.listItemBottomSheetBottomNavigationMoreLayout
        countTextView = binding.listItemBottomSheetBottomNavigationMoreCount
    }

    fun bind(listItem: BottomNavigationItem) {
        titleTextView.text = listItem.title
        iconView.setImageDrawable(UIUtil.getNavListItemDrawable(context, listItem, false))
        listItemBottomSheetBottomNavigationMoreLayout.setOnClickListener {
            context.startActivity(ConnectMainActivity.newIntent(context, getViewsToShow(listItem.itemId)));
        }

        mUnreadCountLiveData.observeUnreadCountLiveData(listItem.itemId){
            setUnreadMessageCountUI(it)
        }

        setContentDescriptions(listItem)
    }

    private fun getViewsToShow(listItemId: ConnectMainViewPagerAdapter.FeatureType): Int {
        return when (listItemId) {
            ConnectMainViewPagerAdapter.FeatureType.Calls -> Enums.Platform.ViewsToShow.CALLS
            ConnectMainViewPagerAdapter.FeatureType.Voicemail -> Enums.Platform.ViewsToShow.CALLS_VOICEMAIL
            ConnectMainViewPagerAdapter.FeatureType.Contacts -> Enums.Platform.ViewsToShow.CONTACTS
            ConnectMainViewPagerAdapter.FeatureType.Messaging -> Enums.Platform.ViewsToShow.MESSAGING
            ConnectMainViewPagerAdapter.FeatureType.Meetings -> Enums.Platform.ViewsToShow.MEETINGS
            ConnectMainViewPagerAdapter.FeatureType.Rooms -> Enums.Platform.ViewsToShow.ROOMS
            ConnectMainViewPagerAdapter.FeatureType.Chat -> Enums.Platform.ViewsToShow.CHAT
            ConnectMainViewPagerAdapter.FeatureType.More -> Enums.Platform.ViewsToShow.MORE
            else -> Enums.Platform.ViewsToShow.MORE
        }
    }

    private fun setContentDescriptions(listItem: BottomNavigationItem) {
        /*TODO: Add content descriptions*/
    }

    private fun setUnreadMessageCountUI(count: Int?) {
        if (count != null && count > 0) {
            countTextView.visibility = View.VISIBLE
            countTextView.text = count.toString()
        } else {
            countTextView.visibility = View.GONE
        }
    }
}