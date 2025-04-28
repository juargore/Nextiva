package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetAllowContactAccessBinding

class BottomSheetAllowContactAccess: BaseBottomSheetDialogFragment(R.id.bottom_sheet_allow_contact_access_scrollview, false) {
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var maybeLater: TextView
    private lateinit var allow: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_allow_contact_access, container, false)
        view?.let { bindViews(view) }

        cancelIcon.setOnClickListener { dismiss() }
        maybeLater.setOnClickListener { dismiss() }
        allow.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
            dismiss()
        }

        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetAllowContactAccessBinding.bind(view)
        cancelIcon = binding.cancelIconInclude.closeIconView
        maybeLater = binding.bottomSheetAllowContactAccessMaybeLater
        allow = binding.bottomSheetAllowContactAccessPrimaryButton
    }

}
