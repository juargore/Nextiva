/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.NextivaAnywhereLocationListItem;
import com.nextiva.nextivaapp.android.databinding.ListItemNextivaAnywhereLocationBinding;
import com.nextiva.nextivaapp.android.view.DetailItemView;
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher;

/**
 * Created by joedephillipo on 2/22/18.
 */

public class NextivaAnywhereLocationViewHolder extends BaseViewHolder<NextivaAnywhereLocationListItem> implements
        View.OnClickListener {

    private final View mMasterItemView;
    protected DetailItemView mDetailItemView;
    protected TextView mEnabledTextView;

    public NextivaAnywhereLocationViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_nextiva_anywhere_location, parent, false),
             context,
             masterListListener);
    }

    private NextivaAnywhereLocationViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);
        bindViews(itemView);
        mMasterItemView = itemView;

        mMasterItemView.setOnClickListener(this);

        mDetailItemView.addSubTitleTextWatcher(new ExtensionEnabledPhoneNumberFormattingTextWatcher());
    }

    @Override
    public void bind(@NonNull NextivaAnywhereLocationListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;

        mMasterItemView.setClickable(mListItem.isClickable());
        mDetailItemView.setTitleText(mListItem.getTitle());

        if (!TextUtils.isEmpty(mListItem.getSubTitle())) {
            mDetailItemView.setSubTitleEnabled(true);
            mDetailItemView.setSubTitleText(mListItem.getSubTitle());

        } else {
            mDetailItemView.setSubTitleEnabled(false);
        }

        mEnabledTextView.setText(mListItem.getLocation().getActive() ? R.string.general_on : R.string.general_off);
    }

    private void bindViews(View view) {
        ListItemNextivaAnywhereLocationBinding binding = ListItemNextivaAnywhereLocationBinding.bind(view);

        mDetailItemView = binding.listItemNextivaAnywhereLocationDetailItemView;
        mEnabledTextView = binding.listItemNextivaAnywhereLocationEnabledTextView;
    }

    // --------------------------------------------------------------------------------------------
    // View.OnClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            mMasterListListener.onDetailItemViewListItemClicked(mListItem);
        }
    }
    // --------------------------------------------------------------------------------------------
}
