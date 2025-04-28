package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailFooterListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemConnectContactDetailFooterBinding
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import org.threeten.bp.DateTimeException
import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeParseException
import javax.inject.Inject

internal class ConnectContactDetailFooterViewHolder private constructor(itemView: View, context: Context, masterListListener: MasterListListener):
        BaseViewHolder<ConnectContactDetailFooterListItem>(itemView, context, masterListListener) {

    private val masterItemView: View

    @Inject
    lateinit var calendarManager: CalendarManager
    @Inject
    lateinit var logManager: LogManager

    private lateinit var createdBy: TextView
    private lateinit var lastModifiedBy: TextView
    private val formatterManager: FormatterManager = FormatterManager.getInstance()

    @Inject
    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_connect_contact_detail_footer,
                    parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        masterItemView = itemView
    }

    override fun bind(listItem: ConnectContactDetailFooterListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        createdBy.text = listItem.data.createdBy

        if (!listItem.data.lastModifiedBy.isNullOrBlank()) {
            if (!listItem.data.lastModifiedOn.isNullOrBlank()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        setLastModifiedText()
                    } catch (e: DateTimeParseException) {
                        lastModifiedBy.text = listItem.data.lastModifiedBy
                        logManager.logToFile(Enums.Logging.STATE_ERROR, "Error parsing date. ${listItem.data.lastModifiedOn ?: "No date found"}")
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                } else {
                    try {
                        setLastModifiedText()
                    } catch (e: DateTimeException) {
                        lastModifiedBy.text = listItem.data.lastModifiedBy
                        logManager.logToFile(Enums.Logging.STATE_ERROR, "Error parsing date. ${listItem.data.lastModifiedOn ?: "No date found"}")
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
            }
        }

        setContentDescriptions()
    }

    fun setLastModifiedText() {
        lastModifiedBy.text = mContext.getString(R.string.connect_contact_details_last_modified_by_text,
                mListItem.data.lastModifiedBy,
                formatterManager.format_humanReadableConnectShortTimeStamp(Instant.parse(mListItem.data.lastModifiedOn)))
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectContactDetailFooterBinding.bind(view)

        createdBy = binding.listItemConnectContactDetailCreatedBy
        lastModifiedBy = binding.listItemConnectContactDetailLastModified
    }

    private fun setContentDescriptions() {
        createdBy.contentDescription = mContext.getString(R.string.connect_contact_details_created_by_content_description, createdBy.text)
        lastModifiedBy.contentDescription = mContext.getString(R.string.connect_contact_details_last_modified_by_content_description, lastModifiedBy.text)
    }
}