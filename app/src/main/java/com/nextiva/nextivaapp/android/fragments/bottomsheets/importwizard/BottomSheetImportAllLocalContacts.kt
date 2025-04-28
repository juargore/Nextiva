package com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.BottomSheetImportAllLocalContactsBinding
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.util.extensions.orZero
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetImportWizardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetImportAllLocalContacts : Fragment() {

    private lateinit var sharedSwitch: SwitchCompat
    private lateinit var messageIcon: FontTextView
    private lateinit var messageText: TextView
    private lateinit var messageLayout: LinearLayout
    private lateinit var importAll: TextView
    private lateinit var backIcon: RelativeLayout
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var cancelButton: TextView

    private var duplicatesStrategy = ""

    private var viewModel: BottomSheetImportWizardViewModel? = null

    companion object {
        const val TAG = "BottomSheetWizardImportAllLocalContacts"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_import_all_local_contacts, container, false)
        view?.let { bindViews(view) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnGlobalLayoutListener( object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                (parentFragment as? BottomSheetImportWizard)?.collapseDialog(
                    view.measuredHeight.orZero(),
                    BottomSheetBehavior.STATE_COLLAPSED
                )
            }
        }
        )
        parentFragment?.let { parent ->
            viewModel = ViewModelProvider(
                parent
            )[BottomSheetImportWizardViewModel::class.java]
            sharedSwitch.isChecked = viewModel?.shareContacts.orFalse()
            duplicatesStrategy = viewModel?.duplicatesStrategy.toString()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel?.shareContacts = sharedSwitch.isChecked
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetImportAllLocalContactsBinding.bind(view)

        sharedSwitch = binding.bottomSheetImportAllSharedSwitch
        messageIcon = binding.bottomSheetImportAllMessageIcon
        messageLayout = binding.bottomSheetImportAllMessageLayout
        messageText = binding.bottomSheetImportAllMessageText
        importAll = binding.bottomSheetImportAllPrimaryButton
        backIcon = binding.backArrowInclude.backArrowView
        cancelIcon = binding.cancelIconInclude.closeIconView
        cancelButton = binding.bottomSheetImportAllCancel

        cancelIcon.setOnClickListener { (parentFragment as? BottomSheetImportWizard)?.dismiss() }
        cancelButton.setOnClickListener { (parentFragment as? BottomSheetImportWizard)?.dismiss() }
        backIcon.setOnClickListener {
            (parentFragment as? BottomSheetImportWizard)?.onBackPressed()
        }

        sharedSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                messageIcon.setTextColor(ContextCompat.getColor(requireActivity(), R.color.connectSecondaryRed))
                messageIcon.setIcon(R.string.fa_exclamation_circle, Enums.FontAwesomeIconType.REGULAR)
                messageLayout.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.connectPrimaryLightRed))
                messageText.text = getString(R.string.connect_import_local_contacts_bottom_sheet_shared_message)

            } else {
                messageIcon.setTextColor(ContextCompat.getColor(requireActivity(), R.color.connectPrimaryBlue))
                messageIcon.setIcon(R.string.fa_info_circle, Enums.FontAwesomeIconType.REGULAR)
                messageLayout.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.connectPrimaryLightBlue))
                messageText.text = getString(R.string.connect_import_local_contacts_bottom_sheet_private_message)
            }
        }

        importAll.setOnClickListener {
            if(viewModel?.selectedContacts?.isNotEmpty().orFalse()) {
                viewModel?.shareContacts = sharedSwitch.isChecked
                (parentFragment as? BottomSheetImportWizard)?.let { parent ->
                    parent.collapseDialog(ViewGroup.LayoutParams.WRAP_CONTENT, BottomSheetBehavior.STATE_HIDDEN)
                    parent.childFragmentManager.popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    parent.setFragment(BottomSheetImportContactLoading(), BottomSheetImportContactLoading.TAG, false)
                }
            }
        }
    }
}