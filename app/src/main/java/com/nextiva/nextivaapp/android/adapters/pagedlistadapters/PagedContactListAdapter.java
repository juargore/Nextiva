package com.nextiva.nextivaapp.android.adapters.pagedlistadapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ContactViewHolder;
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.diffcallbacks.ContactDiffCallback;

public class PagedContactListAdapter extends PagedListAdapter<ContactListItem, ContactViewHolder> {

    private final Context mContext;
    private final MasterListListener mMasterListListener;

    public PagedContactListAdapter(Context context, MasterListListener masterListListener) {
        super(new ContactDiffCallback());
        mContext = context;
        mMasterListListener = masterListListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(parent, mContext, mMasterListListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactListItem listItem = getItem(position);

        if (listItem == null) {
            holder.clear();
        } else {
            holder.bind(listItem);
        }
    }
}
