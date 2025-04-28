package com.nextiva.nextivaapp.android.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.MoreFeatureViewHolder
import com.nextiva.nextivaapp.android.models.BottomNavigationItem

class BottomSheetBottomNavigationMoreAdapter(private val featuresList: List<BottomNavigationItem>, private val unreadCountLiveData: UnreadCountLiveData) : RecyclerView.Adapter<MoreFeatureViewHolder>() {

    interface UnreadCountLiveData {
        fun observeUnreadCountLiveData(featureType: ConnectMainViewPagerAdapter.FeatureType, updateView: (Int) -> Unit )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreFeatureViewHolder {
        return MoreFeatureViewHolder(parent,unreadCountLiveData)
    }

    override fun onBindViewHolder(holder: MoreFeatureViewHolder, position: Int) {
        holder.bind(featuresList[position])
    }

    override fun getItemCount(): Int {
        return featuresList.count()
    }

}