package com.nextiva.nextivaapp.android.features.rooms.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetMessageMenuDialogBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetMessageMenuDialog(
        val editAction: (() -> Unit)?,
        val deleteAction: (() -> Unit)?,
        val downloadAction: (() -> Unit)?,
        val downloadTextString: String? = null,
        val cancelAction: () -> Unit
): BaseBottomSheetDialogFragment() {

    @Inject
    lateinit var dbManager: DbManager

    private lateinit var downloadLayout: ConstraintLayout
    private lateinit var downloadTextView: TextView
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

        downloadLayout = binding.bottomSheetMessageMenuDownloadLayout
        downloadTextView = binding.bottomSheetMessageMenuDownloadTextView
        editLayout = binding.bottomSheetMessageMenuEditLayout
        deleteLayout = binding.bottomSheetMessageMenuDeleteLayout

        if (editAction != null) {
            editLayout.visibility = View.VISIBLE
            editLayout.setOnClickListener {
                isCancelAction = false
                editAction.invoke()
                dismiss()
            }
        }
        if (deleteAction != null) {
            deleteLayout.visibility = View.VISIBLE
            deleteLayout.setOnClickListener {
                isCancelAction = false
                deleteAction.invoke()
                dismiss()
            }
        }
        if (downloadAction != null) {
            downloadLayout.visibility = View.VISIBLE
            downloadTextView.text = downloadTextString
            downloadLayout.setOnClickListener {
                isCancelAction = false
                downloadAction.invoke()
                dismiss()
            }
        }
    }

    override fun onStop() {
        if (isCancelAction) {
            cancelAction.invoke()
        }
        super.onStop()
    }
}
