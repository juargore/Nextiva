package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.BottomSheetMenuAdapter
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BottomSheetMenuListItem
import com.nextiva.nextivaapp.android.databinding.BottomSheetMenuBinding

class BottomSheetMenu(val menuItems: ArrayList<BottomSheetMenuListItem>,
                      val itemClickListener: (BottomSheetMenuListItem) -> (Unit)): BaseBottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_menu, container, false)
        view?.let { bindViews(view) }

        val adapter = BottomSheetMenuAdapter(menuItems) { listItem ->
            itemClickListener(listItem)
            dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetMenuBinding.bind(view)
        recyclerView = binding.bottomSheetMenuRecyclerView
    }
}