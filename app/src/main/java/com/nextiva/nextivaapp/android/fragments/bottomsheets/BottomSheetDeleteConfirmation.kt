package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetDeleteConfirmationBinding
import com.nextiva.nextivaapp.android.viewmodels.DeleteConfirmationActionsViewModel

class BottomSheetDeleteConfirmation: BaseBottomSheetDialogFragment() {

    companion object {
        const val TITLE = "title"
        const val SUBTITLE = "subtitle"
        const val PRIMARY_BUTTON_TEXT = "primary_button_text"
        const val SHOW_SHOW_AGAIN_CHECKBOX = "showShowAgainCheckbox"
        const val SHOW_CLOSE_BUTTON = "showCloseButton"

        fun newInstance(
            title: String? = null,
            subtitle: String? = null,
            primaryButtonText: String? = null,
            showShowAgainCheckbox: Boolean = false,
            showCloseButton: Boolean = true,
            onShowAgainDialogChanged: ((Boolean) -> Unit)? = null,
            cancelAction: (() -> Unit)? = null,
            deleteAction: (() -> Unit)? = null
        ): BottomSheetDeleteConfirmation {

            val fragment = BottomSheetDeleteConfirmation()
            fragment.mDeleteAction = deleteAction
            fragment.mCancelAction = cancelAction
            fragment.mOnShowAgainDialogChanged = onShowAgainDialogChanged

            val args = Bundle()
            args.putString(TITLE, title)
            args.putString(SUBTITLE, subtitle)
            args.putString(PRIMARY_BUTTON_TEXT, primaryButtonText)
            args.putBoolean(SHOW_SHOW_AGAIN_CHECKBOX, showShowAgainCheckbox)
            args.putBoolean(SHOW_CLOSE_BUTTON, showCloseButton)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: DeleteConfirmationActionsViewModel by viewModels()

    private var mTitle: String? = null
    private var mSubtitle: String? = null
    private var mPrimaryButtonText: String? = null
    private var mShowShowAgainCheckbox: Boolean = false
    private var mShowCloseButton: Boolean = false
    private var mOnShowAgainDialogChanged: ((Boolean) -> Unit)? = null
    private var mCancelAction: (() -> Unit)? = null
    private var mDeleteAction: (() -> Unit)? = null
    private var isCancelAction = true

    private lateinit var primaryButton: TextView
    private lateinit var secondaryButton: TextView
    private lateinit var headerLayout: LinearLayout
    private lateinit var titleTextView: TextView
    private lateinit var subtitleTextView: TextView
    private lateinit var titleLayout: LinearLayout
    private lateinit var cancelButton: RelativeLayout
    private lateinit var showAgainCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_delete_confirmation, container, false)
        processArguments()
        view?.let { bindViews(view) }

        setContentDescriptions()

        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetDeleteConfirmationBinding.bind(view)
        primaryButton = binding.bottomSheetDeleteConfirmationPrimary
        secondaryButton = binding.bottomSheetDeleteConfirmationSecondary
        headerLayout = binding.bottomSheetDeleteConfirmationHeaderLayout
        titleLayout = binding.bottomSheetDeleteConfirmationTitleLayout
        titleTextView = binding.bottomSheetDeleteConfirmationTitle
        cancelButton = binding.cancelIconInclude.closeIconView
        subtitleTextView = binding.bottomSheetDeleteConfirmationSubtitle
        showAgainCheckBox = binding.bottomSheetCheckbox

        headerLayout.background = ContextCompat.getDrawable(requireActivity(), if (mTitle == null) R.drawable.rounded_bottom_sheet_background else R.drawable.rounded_bottom_sheet_background_tinted)
        titleLayout.visibility = if (mTitle == null) View.GONE else View.VISIBLE
        subtitleTextView.visibility = if (mSubtitle == null) View.GONE else View.VISIBLE
        titleTextView.text = mTitle
        subtitleTextView.text = mSubtitle

        secondaryButton.setOnClickListener { dismiss() }
        primaryButton.text = mPrimaryButtonText
        primaryButton.setOnClickListener {
            isCancelAction = false
            mDeleteAction?.let { it1 -> it1() }
            dismiss()
        }

        if (mShowShowAgainCheckbox) {
            showAgainCheckBox.visibility = View.VISIBLE
            showAgainCheckBox.setOnCheckedChangeListener { _, changed ->
                mOnShowAgainDialogChanged?.invoke(changed)
            }
        }

        with(cancelButton) {
            if (!mShowCloseButton) visibility = View.GONE
            setOnClickListener { dismiss() }
        }
    }

    private fun setContentDescriptions() {
        primaryButton.contentDescription = primaryButton.text
        secondaryButton.contentDescription = secondaryButton.text
    }

    private fun processArguments() {
        mTitle = arguments?.getString(TITLE)
        mSubtitle = arguments?.getString(SUBTITLE)
        mPrimaryButtonText = arguments?.getString(PRIMARY_BUTTON_TEXT) ?: requireContext().getString(R.string.general_delete)
        mShowShowAgainCheckbox = arguments?.getBoolean(SHOW_SHOW_AGAIN_CHECKBOX) ?: false
        mShowCloseButton = arguments?.getBoolean(SHOW_CLOSE_BUTTON) ?: true

        //saves mDeleteAction, mCancelAction and mOnShowAgainDialogChanged in case of a fragment recreation
        mOnShowAgainDialogChanged?.let { viewModel.onShowAgainDialogChanged = it }
        mCancelAction?.let { viewModel.cancelAction = it }
        mDeleteAction?.let { viewModel.deleteAction = it }

        if (mOnShowAgainDialogChanged == null && viewModel.onShowAgainDialogChanged != null) {
            mOnShowAgainDialogChanged = viewModel.onShowAgainDialogChanged
        }
        if (mCancelAction == null && viewModel.cancelAction != null) {
            mCancelAction = viewModel.cancelAction
        }
        if (mDeleteAction == null && viewModel.deleteAction != null) {
            mDeleteAction = viewModel.deleteAction
        }
    }

    override fun onStop() {
        if (isCancelAction) {
            mCancelAction?.let { it() }
        }
        super.onStop()
    }
}
