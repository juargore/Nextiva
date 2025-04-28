package com.nextiva.nextivaapp.android.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem
import com.nextiva.nextivaapp.android.databinding.DialogConnectMenuBinding

class ConnectMenuDialog(val values: ArrayList<DialogContactActionDetailListItem>, private val valueCallback: ((Any?) -> Unit)): BaseDialog() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_connect_menu, container, false)
        view?.let { bindViews(view) }

        recyclerView.adapter = adapter
        adapter?.updateList(values as List<BaseListItem>?)

        return view
    }

    fun bindViews(view: View) {
        val binding = DialogConnectMenuBinding.bind(view)
        recyclerView = binding.dialogConnectMenuRecyclerView
    }

    override fun onDialogContactActionDetailListItemClicked(listItem: DialogContactActionDetailListItem) {
        super.onDialogContactActionDetailListItemClicked(listItem)
        valueCallback(listItem.data)
        dismiss()
    }
}