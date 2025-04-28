package com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BaseBottomSheetDialogFragment
import com.nextiva.nextivaapp.android.util.extensions.isNull
import com.nextiva.nextivaapp.android.util.extensions.orFalse


class BottomSheetImportWizard : BaseBottomSheetDialogFragment(tintedAppBar = true) {

    companion object {
        private const val HAS_LOCAL_CONTACTS = "has_local_contacts"
        private const val START_IMPORT = "start_importing"
        private const val SHOULD_SELECT_ALL = "should_select_all"
        private const val ENABLE_IMPORT = "enable_import"

        fun newInstance(
            hasLocalContacts: Boolean,
            startImporting: Boolean,
            shouldSelectAll: Boolean = false,
            enableImport: Boolean = true
        ): BottomSheetImportWizard =
            BottomSheetImportWizard().apply {
                arguments = Bundle().apply {
                    putBoolean(HAS_LOCAL_CONTACTS, hasLocalContacts)
                    putBoolean(START_IMPORT, if (shouldSelectAll) true else startImporting)
                    putBoolean(SHOULD_SELECT_ALL, shouldSelectAll)
                    putBoolean(ENABLE_IMPORT, enableImport)
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_view_import_wizard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState.isNull()) {
            setFragment(
                BottomSheetViewLocalContacts(),
                BottomSheetViewLocalContacts.TAG,
                false
            )
        }

        dialog?.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action === KeyEvent.ACTION_UP) {
                onBackPressed()
                true
            } else
                false
        }
    }

    fun onBackPressed() {
        if (!childFragmentManager.popBackStackImmediate()) {
            dismiss()
        }
    }

    fun setFragment(fragment: Fragment, tag: String, addToBackStack: Boolean = true) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.import_framelayout, fragment)
        if (addToBackStack) transaction.addToBackStack(tag)
        transaction.commit()
    }

    fun showFullHeight() {
        (dialog as? BottomSheetDialog)?.let { dialog ->
            setupFullHeight(dialog)
        }
    }

    fun collapseDialog(height: Int, @BottomSheetBehavior.StableState state: Int) {
        (dialog as? BottomSheetDialog)?.let { dialog ->
            collapseToHeight(dialog, height, state)
        }
    }

    fun setDialogState(@BottomSheetBehavior.StableState state: Int) {
        (dialog as? BottomSheetDialog)?.let { dialog ->
            setDialogState(dialog, state)
        }
    }

    fun hasLocalContacts() = arguments?.getBoolean(HAS_LOCAL_CONTACTS).orFalse()

    fun shouldStartImport() = arguments?.getBoolean(START_IMPORT).orFalse()

    fun shouldSelectAll() = arguments?.getBoolean(SHOULD_SELECT_ALL).orFalse()

    fun enableImport() = arguments?.getBoolean(ENABLE_IMPORT).orFalse()


}