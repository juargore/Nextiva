/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.text.TextUtils;
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
import com.nextiva.nextivaapp.android.databinding.ListItemChatMessageReceivedBinding;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.AvatarInfo;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;
import com.nextiva.nextivaapp.android.util.extensions.TextViewExtensionsKt;

import javax.inject.Inject;

/**
 * Created by adammacdonald on 2/9/18.
 */

public class ChatMessageReceivedViewHolder extends ChatMessageViewHolder {

    private final View mMasterItemView;
    @Inject
    protected DbManager mDbManager;
    @Inject
    protected SettingsManager mSettingsManager;

    @Inject
    public ChatMessageReceivedViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_message_received, parent, false),
             context,
             masterListListener);
    }

    private ChatMessageReceivedViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);
        mMasterItemView = itemView;

    }

    @Override
    public void bind(@NonNull ChatMessageListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;
        setContentDescriptions();

        if (mListItem.getBubbleType() == Enums.Chats.MessageBubbleTypes.START) {
            if (mAvatarView != null) {
                mAvatarView.setVisibility(View.VISIBLE);
            }


            if (mAvatarView != null && !TextUtils.isEmpty(mListItem.getData().getFrom())) {
                AvatarInfo.Builder builder = new AvatarInfo.Builder()
                        .setPhotoData(mListItem.getAvatarBytes())
                        .setDisplayName(!TextUtils.isEmpty(mListItem.getData().getUIName()) ? mListItem.getData().getUIName() : mDbManager.getUINameFromJid(mListItem.getData().getFrom()));

                mAvatarView.setAvatar(builder.build());
            }


        } else {
            if (mAvatarView != null) {
                mAvatarView.setVisibility(View.GONE);
            }
        }

        mMessageTextView.setText(mListItem.getData().getBody());
        TextViewExtensionsKt.makeLinkable(mMessageTextView);

        mMessageTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.nextivaPrimaryBlue));
        int topSpacePadding = 0;
        switch (mListItem.getBubbleType()) {
            case Enums.Chats.MessageBubbleTypes.START:
            {
                topSpacePadding = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, mContext.getResources().getDisplayMetrics()) + 0.5f);
                break;
            }
            case Enums.Chats.MessageBubbleTypes.MIDDLE:
            case Enums.Chats.MessageBubbleTypes.END: {
                break;
            }
        }
        mMessageContainer.setBackgroundResource(ApplicationUtil.isNightModeEnabled(mContext, mSettingsManager) ?
                R.drawable.shape_message_received_night :
                R.drawable.shape_message_received);

        mReceivedLayout.setPadding(mReceivedLayout.getPaddingLeft(),
                topSpacePadding,
                mReceivedLayout.getPaddingRight(),
                mReceivedLayout.getPaddingBottom());

        mMessageTextView.setElevation(4.0f);

        if (mUINameTextView != null && mListItem.getData().getFrom() != null)
        {
            mUINameTextView.setText(mDbManager.getUINameFromJid(mListItem.getData().getFrom()));
            if(mUINameTextView.getText().length() > 0)
                mUINameTextView.setVisibility(View.VISIBLE);
        }

        mDatetimeTextView.setText(mListItem.getHumanReadableDatetime());
        mDatetimeTextView.setVisibility(View.GONE);
        mDatetimeTextView.setVisibility(mListItem.getData().showTimeSeparator() ? View.VISIBLE : View.GONE);
    }

    private void setContentDescriptions() {
        mMasterItemView.setContentDescription(mContext.getString(R.string.chat_message_list_item_received_content_description));
        mMessageTextView.setContentDescription(mContext.getString(R.string.chat_message_list_item_message_content_description));
    }

    public void bindViews(View view) {
        ListItemChatMessageReceivedBinding binding = ListItemChatMessageReceivedBinding.bind(view);

        mReceivedLayout = binding.listItemChatMessageReceivedLayout;
        mAvatarView = binding.listItemChatMessageAvatarView;
        mMessageTextView = binding.listItemChatMessageTextView;
        mDatetimeTextView = binding.listItemChatMessageDatetimeTextView;
        mUINameTextView = binding.listItemChatMessageUserTextView;
        mMessageContainer = binding.listItemMessageContainer;
    }
}
