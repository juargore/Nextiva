package com.nextiva.nextivaapp.android.features.messaging.view

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.BottomSheetMenuAdapter
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BottomSheetMenuListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.BottomSheetSelectPhoneBinding
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BaseBottomSheetDialogFragment
import java.util.Locale

class BottomSheetSelectNumber(val phoneNumbers: List<PhoneNumber>,
                              val itemSelectedListener: (PhoneNumber?) -> (Unit)): BaseBottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BottomSheetMenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_select_phone, container, false)
        view?.let { bindViews(view) }

        var numberItems = ArrayList<BottomSheetMenuListItem>()
        phoneNumbers.forEach { number ->
            numberItems.add(
                BottomSheetMenuListItem(
                    PhoneNumberUtils.formatNumber(
                        number.strippedNumber,
                        Locale.getDefault().country
                    ),
                    R.string.fa_comment_dots,
                    getLabel(number.type),
                    getColor(number.type)
                )
            )
        }

        adapter = BottomSheetMenuAdapter(numberItems) { itemSelected(it) }
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetSelectPhoneBinding.bind(view)
        recyclerView = binding.bottomSheetSendToRecyclerView
    }

    private fun itemSelected(listItem: BottomSheetMenuListItem) {
        val strippedSelection = PhoneNumberUtils.stripSeparators(listItem.text)
        phoneNumbers.firstOrNull { it.strippedNumber == strippedSelection }?.let { selectedNumber ->
            itemSelectedListener(selectedNumber)
        }
        dismiss()
    }

    private val typeMap = mapOf(
        Enums.Contacts.PhoneTypes.MOBILE_PHONE to Pair(R.string.connect_create_contact_mobile_phone_type, R.color.phoneTypeMobile),
        Enums.Contacts.PhoneTypes.WORK_PHONE to Pair(R.string.connect_create_contact_work_phone_type, R.color.phoneTypeWork),
        Enums.Contacts.PhoneTypes.HOME_PHONE to Pair(R.string.connect_create_contact_home_phone_type, R.color.phoneTypeHome),
        Enums.Contacts.PhoneTypes.OTHER_PHONE to Pair(R.string.connect_create_contact_other_phone_type, R.color.phoneTypeOther),
    )

    private fun getLabel(type: Int): String? {
        val resId = typeMap[type]?.first ?: return null
        return context?.getString(resId)
    }

    private fun getColor(type: Int): Int? {
        val resId = typeMap[type]?.second ?: return null
        return context?.getColor(resId)
    }
}