package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HealthCheckListItem
import com.nextiva.nextivaapp.android.databinding.ListItemHealthCheckBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class HealthCheckViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener):
    BaseViewHolder<HealthCheckListItem>(itemView, context, masterListListener) {

    private val masterItemView: View

    private lateinit var title: TextView
    private lateinit var timeElapsed: TextView
    private lateinit var passResult: TextView

    private var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("ss.SSS", Locale.getDefault())

    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_health_check, parent, false),
        context,
        masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
    }

    override fun bind(listItem: HealthCheckListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        title.text = listItem.title

        if (listItem.enabled) {
            listItem.clearResult = { clearResult() }
            listItem.updateResult = { setResult(it) }

        } else {
            timeElapsed.visibility = View.GONE
            passResult.text = mContext.getString(R.string.health_check_na)
        }

        setContentDescriptions()
    }

    private fun clearResult() {
        timeElapsed.visibility = View.GONE
        passResult.visibility = View.GONE
    }

    private fun setResult(result: Pair<Long?, Boolean>) {
        passResult.text = if (result.second) mContext.getString(R.string.health_check_pass) else mContext.getString(R.string.health_check_fail)
        passResult.setTextColor(ContextCompat.getColor(mContext, if (result.second) R.color.nextivaGreen else R.color.nextivaRed))

        result.first?.let { time ->
            val timeElapsedString = "(${simpleDateFormat.format(Date(time))
                .removePrefix("0")
                .removeSuffix("0")
                .removeSuffix("0")}s)"
            timeElapsed.text = timeElapsedString
            timeElapsed.visibility = View.VISIBLE
        }

        passResult.visibility = View.VISIBLE
        mListItem.isChecking = false

        setContentDescriptions()
    }

    private fun bindViews(view: View) {
        val binding = ListItemHealthCheckBinding.bind(view)

        title = binding.healthCheckTitle
        timeElapsed = binding.healthCheckTimeElapsed
        passResult = binding.healthCheckPass
    }

    private fun setContentDescriptions() {
        title.contentDescription = title.text
        timeElapsed.contentDescription = "${title.text} ${timeElapsed.text}"
        passResult.contentDescription = "${title.text} ${passResult.text}"
    }
}