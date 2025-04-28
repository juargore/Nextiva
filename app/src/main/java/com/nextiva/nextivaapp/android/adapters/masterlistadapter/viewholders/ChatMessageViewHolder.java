/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatMessageListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.view.AvatarView;

/**
 * Created by adammacdonald on 2/9/18.
 */

public abstract class ChatMessageViewHolder extends BaseViewHolder<ChatMessageListItem> implements
        View.OnClickListener {

    @Nullable
    protected LinearLayout mSentLayout;
    @Nullable
    protected LinearLayout mReceivedLayout;
    @Nullable
    protected AvatarView mAvatarView;
    protected TextView mMessageTextView;
    protected TextView mDatetimeTextView;
    @Nullable
    protected TextView mUINameTextView;
    @Nullable
    protected TextView mFailedMessageTextView;
    @Nullable
    protected TextView mRetryMessageTextView;
    @Nullable
    protected ImageView mFailedMessageIcon;
    protected ConstraintLayout mMessageContainer;


    ChatMessageViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);

        if (this instanceof ChatMessageSentViewHolder) {
            ((ChatMessageSentViewHolder) this).bindViews(itemView);

        } else if (this instanceof ChatMessageReceivedViewHolder) {
            ((ChatMessageReceivedViewHolder) this).bindViews(itemView);
        }

        mMessageTextView.setVisibility(View.VISIBLE);
        mMessageTextView.setTextIsSelectable(true);
        mMessageTextView.setOnClickListener(this);

        if (mSentLayout != null) {
            mSentLayout.setOnClickListener(this);
        }
    }

    // --------------------------------------------------------------------------------------------
    // View.OnClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            if (mListItem.getData().getSentStatus().equals(Enums.Chats.SentStatus.FAILED)) {
                mMasterListListener.onResendFailedChatMessageClicked(mListItem);

            } else {
                mListItem.getData().setShowTimeSeparator(!mListItem.getData().showTimeSeparator());
                mMasterListListener.onChatMessageListItemDatetimeVisibilityToggled(mListItem);
            }
        }
    }
    // --------------------------------------------------------------------------------------------
}
