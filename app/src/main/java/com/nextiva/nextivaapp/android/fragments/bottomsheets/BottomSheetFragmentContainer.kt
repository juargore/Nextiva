package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetFragmentContainerBinding

class BottomSheetFragmentContainer(val fragment: Fragment): BaseBottomSheetDialogFragment() {

    private lateinit var container: FrameLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_fragment_container, container, false)
        view?.let { bindViews(view) }
        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetFragmentContainerBinding.bind(view)
        container = binding.bottomSheetFragmentContainerLayout

        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.bottom_sheet_fragment_container_layout, fragment)
        transaction.commit()
    }
}