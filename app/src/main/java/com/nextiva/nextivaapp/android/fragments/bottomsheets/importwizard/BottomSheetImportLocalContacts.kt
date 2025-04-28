package com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetImportLocalContactsAskBinding
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BaseBottomSheetDialogFragment
import com.nextiva.nextivaapp.android.util.extensions.orZero
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetImportLocalContacts: BaseBottomSheetDialogFragment(R.id.bottom_sheet_import_ask_scrollview, false) {
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var importAllButton: TextView
    private lateinit var selectButton: TextView
    private lateinit var maybeLaterButton: TextView
    private lateinit var titleText: TextView
    private lateinit var messageText: TextView

    companion object {
        const val NUM_OF_CONTACTS = "NUM_OF_CONTACTS"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_import_local_contacts_ask, container, false)
        view?.let { bindViews(view) }

        val numOfContacts = arguments?.getInt(NUM_OF_CONTACTS).orZero()

        titleText.text = if (numOfContacts > 1) {
            context?.getString(R.string.connect_import_local_contacts_bottom_sheet_title, numOfContacts)
        } else {
            context?.getString(R.string.connect_import_local_contacts_bottom_sheet_single_title, numOfContacts)
        }

        messageText.text = if (numOfContacts > 1) {
            context?.getString(R.string.connect_import_local_contacts_bottom_sheet_message)
        } else {
            context?.getString(R.string.connect_import_local_contacts_bottom_sheet_single_message)
        }

        cancelIcon.setOnClickListener { dismiss() }
        maybeLaterButton.setOnClickListener {
            dismiss()
        }

        importAllButton.setOnClickListener { startImportWizard(true) }
        selectButton.setOnClickListener { startImportWizard(false) }

        return view
    }

    private fun startImportWizard(shouldSelectAll: Boolean) {
        BottomSheetImportWizard.newInstance(
            hasLocalContacts = true,
            startImporting = true,
            shouldSelectAll = shouldSelectAll
        ).show(requireActivity().supportFragmentManager, null)
        dismiss()
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetImportLocalContactsAskBinding.bind(view)
        importAllButton = binding.bottomSheetImportAskPrimaryButton
        selectButton = binding.bottomSheetImportAskSecondaryButton
        maybeLaterButton = binding.bottomSheetImportAskMaybeLater
        cancelIcon = binding.cancelIconInclude.closeIconView
        titleText = binding.bottomSheetImportAskTitle
        messageText = binding.bottomSheetImportAskMessage
    }
}