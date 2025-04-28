/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatMessageListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.ListItemChatMessageSentBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;
import com.nextiva.nextivaapp.android.util.extensions.TextViewExtensionsKt;

import javax.inject.Inject;

/**
 * Created by adammacdonald on 2/9/18.
 */

public class ChatMessageSentViewHolder extends ChatMessageViewHolder {

    @Inject
    protected SettingsManager mSettingsManager;

    private final View mMasterItemView;

    @Inject
    public ChatMessageSentViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_message_sent, parent, false),
             context,
             masterListListener);
    }

    private ChatMessageSentViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);

        mMasterItemView = itemView;
    }

    @Override
    public void bind(@NonNull ChatMessageListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;
        setContentDescriptions();

        if (mFailedMessageIcon != null && mFailedMessageTextView != null && mRetryMessageTextView != null) {
            mFailedMessageIcon.setVisibility(mListItem.getData().getSentStatus() == Enums.Chats.SentStatus.FAILED ?
                    View.VISIBLE : View.GONE);
            mFailedMessageTextView.setVisibility(mListItem.getData().getSentStatus() == Enums.Chats.SentStatus.FAILED ?
                    View.VISIBLE : View.GONE);
            mRetryMessageTextView.setVisibility(mListItem.getData().getSentStatus() == Enums.Chats.SentStatus.FAILED ?
                    View.VISIBLE : View.GONE);
        }

        mMessageTextView.setText(mListItem.getData().getBody());
        TextViewExtensionsKt.makeLinkable(mMessageTextView);

        mMessageTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.nextivaPrimaryBlue));
        int topSpacePadding = 0;
        switch (mListItem.getBubbleType()) {
            case Enums.Chats.MessageBubbleTypes.START: {
                topSpacePadding = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, mContext.getResources().getDisplayMetrics()) + 0.5f);
                break;
            }
            case Enums.Chats.MessageBubbleTypes.MIDDLE:
            case Enums.Chats.MessageBubbleTypes.END: {
                break;
            }
        }

        mMessageContainer.setBackgroundResource(ApplicationUtil.isNightModeEnabled(mContext, mSettingsManager) ?
                R.drawable.shape_message_sent_night :
                R.drawable.shape_message_sent);

        mSentLayout.setPadding(mSentLayout.getPaddingLeft(),
                topSpacePadding,
                mSentLayout.getPaddingRight(),
                mSentLayout.getPaddingBottom());


        if (mUINameTextView != null) {
            mUINameTextView.setVisibility(View.GONE);
        }

        mDatetimeTextView.setText(mListItem.getHumanReadableDatetime());
        mDatetimeTextView.setVisibility(View.GONE);

        //mDatetimeTextView.setVisibility(mListItem.getData().showTimeSeparator() ? View.VISIBLE : View.GONE);
    }

    private void setContentDescriptions() {
        mMasterItemView.setContentDescription(mContext.getString(R.string.chat_message_list_item_sent_content_description));
        mMessageTextView.setContentDescription(mContext.getString(R.string.chat_message_list_item_message_content_description));
    }

    public void bindViews(View view) {
        ListItemChatMessageSentBinding binding = ListItemChatMessageSentBinding.bind(view);

        mSentLayout = binding.listItemChatMessageSentLayout;
        mMessageTextView = binding.listItemChatMessageTextView;
        mDatetimeTextView = binding.listItemChatMessageDatetimeTextView;
        mFailedMessageTextView = binding.listItemChatMessageFailedTextView;
        mRetryMessageTextView = binding.listItemChatMessageFailedRetry;
        mFailedMessageIcon = binding.listItemChatMessageFailedIcon;
        mMessageContainer = binding.listItemMessageContainer;
    }
}
