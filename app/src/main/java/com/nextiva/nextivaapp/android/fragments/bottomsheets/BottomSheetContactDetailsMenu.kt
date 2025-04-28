package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetMessageMenuDialogBinding
import com.nextiva.nextivaapp.android.db.DbManager
import javax.inject.Inject

class BottomSheetContactDetailsMenu(val isPrivate: Boolean, val editAction: () -> Unit, val deleteAction: () -> Unit, val cancelAction: () -> Unit): BaseBottomSheetDialogFragment() {

    @Inject
    lateinit var dbManager: DbManager

    private lateinit var editLayout: ConstraintLayout
    private lateinit var deleteLayout: ConstraintLayout

    private var isCancelAction = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_message_menu_dialog, container, false)
        view?.let { bindViews(view) }

        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetMessageMenuDialogBinding.bind(view)

        editLayout = binding.bottomSheetMessageMenuEditLayout
        deleteLayout = binding.bottomSheetMessageMenuDeleteLayout

        editLayout.visibility = View.VISIBLE
        deleteLayout.visibility = if (isPrivate) View.VISIBLE else View.GONE

        editLayout.setOnClickListener {
            isCancelAction = false
            editAction()
            dismiss()
        }
        deleteLayout.setOnClickListener {
            isCancelAction = false
            deleteAction()
            dismiss()
        }
    }

    override fun onStop() {
        if (isCancelAction) {
            cancelAction()
        }
        super.onStop()
    }
}