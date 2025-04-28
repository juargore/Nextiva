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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallDetailDatetimeListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.ListItemCallDetailsDatetimeBinding;

public class CallDetailDatetimeViewHolder extends BaseViewHolder<CallDetailDatetimeListItem> {

    protected TextView mTitleTextView;
    protected ImageView mCallTypeImageView;
    protected TextView mSubTitleTextView;

    public CallDetailDatetimeViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_call_details_datetime, parent, false),
             context,
             masterListListener);
    }

    private CallDetailDatetimeViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);
        bindViews(itemView);
    }

    @Override
    public void bind(@NonNull CallDetailDatetimeListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;

        if (!TextUtils.isEmpty(mListItem.getCallType())) {
            switch (mListItem.getCallType()) {
                case Enums.Calls.CallTypes.PLACED: {
                    mTitleTextView.setText(R.string.call_details_call_type_placed_title);
                    mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.nextivaOrange));
                    mCallTypeImageView.setImageResource(R.drawable.ic_call_made);
                    mCallTypeImageView.clearColorFilter();
                    break;
                }
                case Enums.Calls.CallTypes.RECEIVED: {
                    mTitleTextView.setText(R.string.call_details_call_type_received_title);
                    mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.nextivaOrange));
                    mCallTypeImageView.setImageResource(R.drawable.ic_call_received);
                    mCallTypeImageView.clearColorFilter();
                    break;
                }
                case Enums.Calls.CallTypes.MISSED: {
                    mTitleTextView.setText(R.string.call_details_call_type_missed_title);
                    mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.errorRed));
                    mCallTypeImageView.setImageResource(R.drawable.ic_call_missed);
                    mCallTypeImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.errorRed));
                    break;
                }
            }

        } else {
            mTitleTextView.setText(null);
            mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.nextivaOrange));
            mCallTypeImageView.setImageDrawable(null);
            mCallTypeImageView.clearColorFilter();
        }

        mSubTitleTextView.setText(mListItem.getSubTitle());
    }

    private void bindViews(View view) {
        ListItemCallDetailsDatetimeBinding binding = ListItemCallDetailsDatetimeBinding.bind(view);

        mTitleTextView = binding.listItemCallDetailsDatetimeTitleTextView;
        mCallTypeImageView = binding.listItemCallDetailsDatetimeCallTypeImageView;
        mSubTitleTextView = binding.listItemCallDetailsDatetimeSubTitleTextView;
    }
}