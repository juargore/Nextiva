package com.nextiva.nextivaapp.android.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.viewholders.TwoLineListItemViewHolder

class SmsViewParticipantsAdapter(private val nameList: List<String>, private val numberList: List<String>) : RecyclerView.Adapter<TwoLineListItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TwoLineListItemViewHolder {
        return TwoLineListItemViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_two_line, parent, false))
    }

    override fun onBindViewHolder(twoLineListItemViewHolder: TwoLineListItemViewHolder, position: Int) {
        if (numberList.isNotEmpty() && position >= 0 && (position < nameList.size) && (position < numberList.size)) {
            if (TextUtils.equals(CallUtil.getStrippedPhoneNumber(nameList[position]), CallUtil.getStrippedPhoneNumber(numberList[position]))) {
                twoLineListItemViewHolder.mTitleTextView.text = CallUtil.phoneNumberFormatNumberDefaultCountry(CallUtil.getStrippedPhoneNumber(numberList[position]))

            } else {
                twoLineListItemViewHolder.mTitleTextView.text = nameList[position]
                twoLineListItemViewHolder.mSubTitleTextView.text = CallUtil.phoneNumberFormatNumberDefaultCountry(CallUtil.getStrippedPhoneNumber(numberList[position]))
            }
        }
    }

    override fun getItemCount(): Int {
        return numberList.size
    }

}