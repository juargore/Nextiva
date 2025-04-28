package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.FeatureFlagListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemFeatureFlagBinding

internal class FeatureFlagViewHolder private constructor(itemView: View, var context: Context, masterListListener: MasterListListener) : BaseViewHolder<FeatureFlagListItem>(itemView, context, masterListListener) {
    private val mMasterItemView: View

    lateinit var title: TextView
    lateinit var enabled: TextView
    lateinit var checkBox: CheckBox
    lateinit var checkboxLinearLayout: LinearLayout

    constructor(parent: ViewGroup, context: Context, masterListListener: MasterListListener) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_feature_flag, parent, false),
            context,
            masterListListener)

    init {
        bindViews(itemView)
        mMasterItemView = itemView
    }

    override fun bind(listItem: FeatureFlagListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        title.text = listItem.data

        when (listItem.enabled) {
            Enums.Platform.FeatureFlagState.ENABLED -> {
                enabled.text = context.getString(R.string.general_enabled)
                enabled.setTextColor(ContextCompat.getColor(context, R.color.nextivaGreen))
            }
            Enums.Platform.FeatureFlagState.DISABLED -> {
                enabled.text = context.getString(R.string.general_disabled)
                enabled.setTextColor(ContextCompat.getColor(context, R.color.nextivaRed))
            }
            Enums.Platform.FeatureFlagState.NOT_FOUND -> {
                enabled.text = context.getString(R.string.feature_flag_not_found)
                enabled.setTextColor(ContextCompat.getColor(context, R.color.grey))
            }
        }

        checkBox.isChecked = listItem.manuallyDisabled

        setContentDescriptions()
    }

    private fun bindViews(view: View) {
        val binding = ListItemFeatureFlagBinding.bind(view)

        title = binding.featureFlagTitle
        enabled = binding.featureFlagEnabled
        checkBox = binding.featureFlagCheck
        checkboxLinearLayout = binding.featureFlagCheckboxLinearLayout

        checkBox.setOnCheckedChangeListener { _, b ->
            mListItem.manuallyDisabled = b
            mMasterListListener?.onFeatureFlagListItemChecked(mListItem)
        }


        if (!TextUtils.equals(context.getString(R.string.app_environment), context.getString(R.string.environment_prod))) {
            checkboxLinearLayout.visibility = View.VISIBLE
        }
        else
        {
            checkboxLinearLayout.visibility = View.GONE

        }
    }

    fun setContentDescriptions() {
        title.contentDescription = mListItem.data
        enabled.contentDescription = mListItem.data + " " + enabled.text
    }
}