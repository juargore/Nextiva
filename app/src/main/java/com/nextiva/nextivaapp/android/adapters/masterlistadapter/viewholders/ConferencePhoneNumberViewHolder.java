/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConferencePhoneNumberListItem;
import com.nextiva.nextivaapp.android.databinding.ListItemConferencePhoneNumberBinding;
import com.nextiva.nextivaapp.android.view.DetailItemView;

/**
 * Created by joedephillipo on 2/22/18.
 */

public class ConferencePhoneNumberViewHolder extends BaseViewHolder<ConferencePhoneNumberListItem> implements
        View.OnClickListener,
        View.OnLongClickListener {

    private final View mMasterItemView;
    protected DetailItemView mDetailItemView;
    protected TextView mConferenceIdTextView;
    protected TextView mSecurityPinTextView;

    public ConferencePhoneNumberViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_conference_phone_number, parent, false),
             context,
             masterListListener);
    }

    private ConferencePhoneNumberViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);
        bindViews(itemView);
        mMasterItemView = itemView;

        mMasterItemView.setOnClickListener(this);
        mMasterItemView.setOnLongClickListener(this);
        mDetailItemView.setOnAction1ClickListener(this);
        mDetailItemView.setOnAction2ClickListener(this);
    }

    @Override
    public void bind(@NonNull ConferencePhoneNumberListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;

        mMasterItemView.setClickable(mListItem.isClickable());
        mMasterItemView.setLongClickable(mListItem.isLongClickable());
        mDetailItemView.setTitleText(mListItem.getTitle());
        mDetailItemView.setSubTitleText(mListItem.getSubTitle());
        mConferenceIdTextView.setText(mListItem.getConferenceId());
        mSecurityPinTextView.setText(mListItem.getSecurityPin());
    }

    private void bindViews(View view) {
        ListItemConferencePhoneNumberBinding binding = ListItemConferencePhoneNumberBinding.bind(view);

        mDetailItemView = binding.listItemConferenceDetailItemView;
        mConferenceIdTextView = binding.listItemConferenceConferenceIdValueTextView;
        mSecurityPinTextView = binding.listItemConferenceSecurityPinValueTextView;
    }

    // --------------------------------------------------------------------------------------------
    // View.OnClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            if (v.getId() == R.id.detail_item_view_action1_image_button) {
                mMasterListListener.onDetailItemViewListItemAction1ButtonClicked(mListItem);

            } else if (v.getId() == R.id.detail_item_view_action2_image_button) {
                mMasterListListener.onDetailItemViewListItemAction2ButtonClicked(mListItem);

            } else {
                mMasterListListener.onDetailItemViewListItemClicked(mListItem);
            }
        }
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // View.OnLongClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean onLongClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            mMasterListListener.onDetailItemViewListItemLongClicked(mListItem);
        }

        return true;
    }
    // --------------------------------------------------------------------------------------------
}
