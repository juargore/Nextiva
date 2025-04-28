package com.nextiva.nextivaapp.android.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BottomSheetMenuListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.BottomSheetMenuViewHolder

class BottomSheetMenuAdapter(val menuItems: ArrayList<BottomSheetMenuListItem>,
                             private val itemClickListener: (BottomSheetMenuListItem) -> Unit) : RecyclerView.Adapter<BottomSheetMenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetMenuViewHolder {
        return BottomSheetMenuViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BottomSheetMenuViewHolder, position: Int) {
        holder.bind(menuItems[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return menuItems.count()
    }
}