package com.nextiva.nextivaapp.android.features.messaging.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.BottomSheetMenuAdapter
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BottomSheetMenuListItem
import com.nextiva.nextivaapp.android.databinding.BottomSheetSendToFromBinding
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BaseBottomSheetDialogFragment
import com.nextiva.nextivaapp.android.models.SendToFromItem

class BottomSheetSendToFrom: BaseBottomSheetDialogFragment() {

    companion object {
        private const val BOTTOM_SHEET_ARGUMENTS = "BottomSheetSendToFromArguments"
        fun newInstance(sendToFromItem: SendToFromItem): BottomSheetSendToFrom {
            return BottomSheetSendToFrom().apply {
                arguments = Bundle().apply {
                    putSerializable(BOTTOM_SHEET_ARGUMENTS,sendToFromItem)
                }
            }
        }
    }

    private lateinit var toItems: ArrayList<BottomSheetMenuListItem>
    private lateinit var fromItems: ArrayList<BottomSheetMenuListItem>
    private var hasDisabledPrimaryNumber: Boolean = false
    private var shouldShowMultipleContacts: Boolean = false
    private lateinit var doneClickListener: (BottomSheetMenuListItem?, BottomSheetMenuListItem?) -> (Unit)

    private lateinit var fromHeader: LinearLayout
    private lateinit var fromRecyclerView: RecyclerView
    private lateinit var toHeader: LinearLayout
    private lateinit var toRecyclerView: RecyclerView
    private lateinit var doneButton: TextView
    private lateinit var divider: LinearLayout
    private lateinit var fromAdapter: BottomSheetMenuAdapter
    private lateinit var toAdapter: BottomSheetMenuAdapter
    private lateinit var multipleContactsText: TextView

    var shouldShowToItems = false
    var shouldShowFromItems = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let {
            val arg = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(BOTTOM_SHEET_ARGUMENTS, SendToFromItem::class.java)
            } else {
                it.getSerializable(BOTTOM_SHEET_ARGUMENTS) as? SendToFromItem
            }
            toItems = arg?.toItems ?: arrayListOf()
            fromItems = arg?.fromItems ?: arrayListOf()
            hasDisabledPrimaryNumber = arg?.hasDisabledPrimaryNumber ?: false
            shouldShowMultipleContacts = arg?.shouldShowMultipleContacts ?: false
            doneClickListener = arg?.doneClickListener ?: { _, _ -> }
        }
        val view = inflater.inflate(R.layout.bottom_sheet_send_to_from, container, false)
        view?.let { bindViews(view) }

        shouldShowFromItems = (hasDisabledPrimaryNumber && fromItems.size == 1) || fromItems.size > 1
        shouldShowToItems = toItems.size > 1

        if (shouldShowFromItems) {
            fromAdapter = BottomSheetMenuAdapter(fromItems) { fromItemSelected(it) }
            fromRecyclerView.layoutManager = LinearLayoutManager(activity)
            fromRecyclerView.adapter = fromAdapter
            fromAdapter.notifyDataSetChanged()

        } else {
            fromRecyclerView.visibility = View.GONE
            fromHeader.visibility = View.GONE
        }

        if (shouldShowToItems) {
            toAdapter = BottomSheetMenuAdapter(toItems) { toItemSelected(it) }
            toRecyclerView.layoutManager = LinearLayoutManager(activity)
            toRecyclerView.adapter = toAdapter
            toAdapter.notifyDataSetChanged()

        } else if (shouldShowMultipleContacts) {
            toRecyclerView.visibility = View.GONE
            multipleContactsText.visibility = View.VISIBLE

        } else {
            toHeader.visibility = View.GONE
            toRecyclerView.visibility = View.GONE
        }

        if (!shouldShowToItems || !shouldShowFromItems) {
            doneButton.visibility = View.GONE
            divider.visibility = View.GONE
        }

        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetSendToFromBinding.bind(view)
        fromHeader = binding.bottomSheetSendFromLayout
        fromRecyclerView = binding.bottomSheetSendFromRecyclerView
        toHeader = binding.bottomSheetSendToLayout
        toRecyclerView = binding.bottomSheetSendToRecyclerView
        doneButton = binding.bottomSheetSendToDone
        divider = binding.bottomSheetSendFromDivider
        multipleContactsText = binding.bottomSheetSendToMultipleContacts

        doneButton.setOnClickListener {
            doneClickListener(toItems.firstOrNull { it.isChecked }, fromItems.firstOrNull { it.isChecked })
            dismiss()
        }
    }

    private fun toItemSelected(listItem: BottomSheetMenuListItem) {
        if (!shouldShowFromItems) {
            doneClickListener(listItem, fromItems.firstOrNull { it.isChecked })
            dismiss()

        } else {
            toAdapter.menuItems.forEach { it.isChecked = listItem == it }
            toAdapter.notifyDataSetChanged()
        }
    }

    private fun fromItemSelected(listItem: BottomSheetMenuListItem) {
        if (!shouldShowToItems) {
            doneClickListener(toItems.firstOrNull { it.isChecked }, listItem)
            dismiss()

        } else {
            fromAdapter.menuItems.forEach { it.isChecked = listItem == it }
            fromAdapter.notifyDataSetChanged()
        }
    }
}