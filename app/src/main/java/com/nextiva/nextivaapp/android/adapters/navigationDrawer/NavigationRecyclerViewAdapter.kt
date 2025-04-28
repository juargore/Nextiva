package com.nextiva.nextivaapp.android.adapters.navigationDrawer

import android.content.Context
import android.graphics.PorterDuff
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R

class NavigationRecyclerViewAdapter(private var items: ArrayList<NavigationItemModel>, var clickCallback: ((NavigationItemModel) -> Unit)?) :RecyclerView.Adapter<NavigationRecyclerViewAdapter.NavigationItemViewHolder>() {

    constructor(items: ArrayList<NavigationItemModel>) : this (items, null)

    private lateinit var context: Context

    class NavigationItemViewHolder(itemView: View, var position: Int?) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationItemViewHolder {
        context = parent.context
        val navItem = LayoutInflater.from(parent.context).inflate(R.layout.list_item_navigation_drawer, parent, false)
        return NavigationItemViewHolder(navItem, null)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: NavigationItemViewHolder, position: Int) {
        val isSignOutItem = TextUtils.equals(items[position].title, context.getString(R.string.main_nav_sign_out))

        holder.position = position
        items[position].position = position

        val divider = holder.itemView.findViewById<View>(R.id.list_item_navigation_divider)
        val title = holder.itemView.findViewById<TextView>(R.id.list_item_navigation_title)
        val icon = holder.itemView.findViewById<ImageView>(R.id.list_item_navigation_icon)

        divider.visibility = if (isSignOutItem) { View.VISIBLE } else { View.GONE }

        if (isSignOutItem) {
            title.setTextColor(ContextCompat.getColor(context, R.color.navigationDrawerSignOut))
            icon.setColorFilter(ContextCompat.getColor(context, R.color.navigationDrawerSignOut), PorterDuff.Mode.SRC_IN)
        }

        title.text = items[position].title
        icon.setImageResource(items[position].icon)

        holder.itemView.setOnClickListener { clickCallback?.let { it(items[position]) } }
    }
}