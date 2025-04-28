package com.nextiva.nextivaapp.android.core.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetAttachmentMenuDialogBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetAttachmentMenuDialog(val takePictureAction: (() -> Unit)?, val choosePhotoAction: (() -> Unit)?, val attachFileAction: (() -> Unit)?, val choosePhotoString: String): BaseBottomSheetDialogFragment() {

    @Inject
    lateinit var dbManager: DbManager

    private lateinit var takePictureLayout: ConstraintLayout
    private lateinit var choosePhotoLayout: ConstraintLayout
    private lateinit var attachFileLayout: ConstraintLayout
    private lateinit var choosePhotoText: TextView

    constructor():this(
        null,
        null,
        null,
        ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_attachment_menu_dialog, container, false)
        view?.let { bindViews(view) }

        return view
    }

    private fun bindViews(view: View) {
        if(takePictureAction == null && choosePhotoAction == null && attachFileAction == null){
            dismiss()
        }

        val binding = BottomSheetAttachmentMenuDialogBinding.bind(view)

        takePictureLayout = binding.bottomSheetTakePictureLayout
        choosePhotoLayout = binding.bottomSheetChoosePhotoLayout
        attachFileLayout = binding.bottomSheetAttachFileLayout

        choosePhotoText = binding.bottomSheetChoosePhotoText
        choosePhotoText.text = choosePhotoString

        takePictureLayout.visibility = if (takePictureAction == null) View.GONE else View.VISIBLE
        choosePhotoLayout.visibility = if (choosePhotoAction == null) View.GONE else View.VISIBLE
        attachFileLayout.visibility = if (attachFileAction == null) View.GONE else View.VISIBLE

        takePictureLayout.setOnClickListener {
            takePictureAction?.let { it() }
            dismiss()
        }
        choosePhotoLayout.setOnClickListener {
            choosePhotoAction?.let { it() }
            dismiss()
        }
        attachFileLayout.setOnClickListener {
            attachFileAction?.let { it() }
            dismiss()
        }
    }

}