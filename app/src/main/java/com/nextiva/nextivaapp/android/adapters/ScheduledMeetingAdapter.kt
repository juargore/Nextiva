package com.nextiva.nextivaapp.android.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ScheduledMeetingViewHolder
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventDetail

class ScheduledMeetingAdapter(private val meetingList: ArrayList<CalendarApiEventDetail>) : RecyclerView.Adapter<ScheduledMeetingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduledMeetingViewHolder {
        return ScheduledMeetingViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ScheduledMeetingViewHolder, position: Int) {
        holder.bind(meetingList[position])
    }

    override fun getItemCount(): Int {
        return meetingList.count()
    }

}