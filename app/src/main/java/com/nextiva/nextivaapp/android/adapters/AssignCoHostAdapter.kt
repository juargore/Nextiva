package com.nextiva.nextivaapp.android.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.AssignCoHostViewHolder
import com.nextiva.nextivaapp.android.meetings.data.CoHostItem

class AssignCoHostAdapter(private val attendeesList: ArrayList<CoHostItem>, private val mItemClickListener: ItemClickListener) :  RecyclerView.Adapter<AssignCoHostViewHolder>() {

    interface ItemClickListener{
        fun onItemClick()
        fun progressBarIsVisible(): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignCoHostViewHolder {
        return AssignCoHostViewHolder(parent,mItemClickListener)
    }

    override fun onBindViewHolder(holder: AssignCoHostViewHolder, position: Int) {
        holder.bind(attendeesList[position])
    }

    override fun getItemCount(): Int {
        return attendeesList.count()
    }

}