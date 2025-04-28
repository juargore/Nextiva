package com.nextiva.nextivaapp.android.adapters.pagedlistadapters.diffcallbacks;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem;

public class ContactDiffCallback extends DiffUtil.ItemCallback<ContactListItem> {

    @Override
    public boolean areItemsTheSame(@NonNull ContactListItem oldItem, @NonNull ContactListItem newItem) {
        return TextUtils.equals(oldItem.getData().getUserId(), newItem.getData().getUserId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull ContactListItem oldItem, @NonNull ContactListItem newItem) {
        return oldItem.getData().equals(newItem.getData()) && TextUtils.equals(oldItem.getSearchTerm(), newItem.getSearchTerm());
    }
}
