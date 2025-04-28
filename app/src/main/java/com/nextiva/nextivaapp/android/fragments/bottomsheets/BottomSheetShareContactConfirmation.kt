package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetShareContactConfirmationBinding
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetShareContactConfirmation(val onOptionSelected: (Boolean) -> Unit): BaseBottomSheetDialogFragment() {

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    private lateinit var cancelIcon: RelativeLayout
    private lateinit var checkBox: CheckBox
    private lateinit var shareButton: TextView
    private lateinit var cancelButton: TextView

    private var optionSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_share_contact_confirmation, container, false)
        view?.let { bindViews(view) }
        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (!optionSelected) {
            onOptionSelected(false)
        }

        if (checkBox.isChecked) {
            sharedPreferencesManager.setBoolean(SharedPreferencesManager.CONNECT_CONTACT_SHARED_DIALOG_DONT_SHOW, true)
        }
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetShareContactConfirmationBinding.bind(view)

        cancelIcon = binding.cancelIconInclude.closeIconView
        checkBox = binding.bottomSheetShareContactConfirmationDontShow
        shareButton = binding.bottomSheetShareContactConfirmationShareButton
        cancelButton = binding.bottomSheetShareContactConfirmationCancelButton

        cancelIcon.setOnClickListener {
            onOptionSelected(false)
            optionSelected = true
            dismiss()
        }

        cancelButton.setOnClickListener {
            onOptionSelected(false)
            optionSelected = true
            dismiss()
        }

        shareButton.setOnClickListener {
            onOptionSelected(true)
            optionSelected = true
            dismiss()
        }
    }
}