/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatConversationListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.ListItemChatConversationBinding;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.AvatarInfo;
import com.nextiva.nextivaapp.android.util.StringUtil;
import com.nextiva.nextivaapp.android.view.AvatarView;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by joedephillipo on 2/26/18.
 */

public class ChatConversationViewHolder extends BaseViewHolder<ChatConversationListItem> implements
        View.OnClickListener,
        View.OnLongClickListener {

    private static final int AVATAR_VIEW_WIDTH_DP = 48;
    private static final int PARTICIPANT_TEXTVIEW_MARGINS_DP = 12;
    private static final int LIST_ITEM_PADDING_DP = 32;

    private final View mMasterItemView;

    protected AvatarView mAvatarView;
    protected TextView mTitleTextView;
    protected TextView mSubtitleTextView;
    protected TextView mTimeTextView;
    protected ImageView mRoomImageView;
    protected TextView txtUnreadChatMessagesCount;

    @Inject
    protected XMPPConnectionActionManager mXMPPConnectionActionManager;
    @Inject
    protected DbManager mDbManager;
    @Inject
    protected SessionManager mSessionManager;
    @Inject
    protected SettingsManager mSettingsManager;
    @Inject
    protected AvatarManager mAvatarManager;

    public ChatConversationViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_conversation, parent, false),
                context,
                masterListListener);
    }

    @Inject
    public ChatConversationViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);
        bindViews(itemView);

        mMasterItemView = itemView;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void bind(@NonNull ChatConversationListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;


        if (!TextUtils.isEmpty(mListItem.getData().getConversationType())) {
            switch (mListItem.getData().getConversationType()) {
                case Enums.Chats.ConversationTypes.CHAT: {
                    if (mAvatarView != null) {
                        if (mListItem.getData().getMembersList() != null && mListItem.getData().getMembersList().size() > 0) {
                            AvatarInfo.Builder builder = new AvatarInfo.Builder()
                                    .setPhotoData(mDbManager.getVCardInThread(mListItem.getData().getChatWith()) != null ? mDbManager.getVCardInThread(mListItem.getData().getChatWith()).getPhotoData() : null);

                            if (!TextUtils.isEmpty(mListItem.getDisplayName())) {
                                mTitleTextView.setText(mListItem.getDisplayName());
                                builder.setDisplayName(mListItem.getDisplayName());

                            } else {
                                mTitleTextView.setText(mListItem.getData().getMembersList().get(0));
                                builder.setDisplayName(mListItem.getData().getMembersList().get(0));
                            }

                            if (mListItem.getPresence() != null) {
                                builder.setPresence(mListItem.getPresence());
                            }

                            mAvatarView.setAvatar(builder.build());

                        } else {
                            mAvatarView.setAvatar(new AvatarInfo.Builder().build());
                            mTitleTextView.setText("");
                        }
                    }

                    mRoomImageView.setVisibility(View.GONE);

                    break;
                }
                case Enums.Chats.ConversationTypes.GROUP_CHAT: {
                    if (mListItem.getData().isRoomGroupChat()) {
                        String jid = mListItem.getData().getRoomOwnerJid();

                        mTitleTextView.setText(!TextUtils.isEmpty(jid) ?
                                mContext.getString(R.string.chats_found_room_title,
                                        TextUtils.isEmpty(mDbManager.getUINameFromJid(jid)) ? jid : mDbManager.getUINameFromJid(jid)) :
                                mContext.getString(R.string.chats_room_title));

                        if (mAvatarView != null) {
                            AvatarInfo avatarInfo;

                            if (mSessionManager.getUserDetails() != null && TextUtils.equals(jid, mSessionManager.getUserDetails().getImpId())) {
                                avatarInfo = new AvatarInfo.Builder()
                                        .setDisplayName(!TextUtils.isEmpty(jid) && mListItem.getData().getMembersList() != null
                                                && !mListItem.getData().getMembersList().isEmpty() ?
                                                TextUtils.isEmpty(mDbManager.getUINameFromJid(jid)) ? jid : mDbManager.getUINameFromJid(jid) :
                                                mListItem.getData().getMembersList().get(0))
                                        .setPhotoData(mAvatarManager.stringToByteArray(mDbManager.getOwnVCardInThread() != null ?
                                                (mDbManager.getOwnVCardInThread().getValue() != null ? mDbManager.getOwnVCardInThread().getValue() : null) :
                                                null))
                                        .build();

                            } else {
                                avatarInfo = new AvatarInfo.Builder()
                                        .setDisplayName(!TextUtils.isEmpty(jid) && mListItem.getData().getMembersList() != null
                                                && !mListItem.getData().getMembersList().isEmpty() ?
                                                TextUtils.isEmpty(mDbManager.getUINameFromJid(jid)) ? jid : mDbManager.getUINameFromJid(jid) :
                                                mListItem.getData().getMembersList().get(0))
                                        .setPhotoData(!TextUtils.isEmpty(jid) && mDbManager.getVCardInThread(jid) != null ?
                                                mDbManager.getVCardInThread(jid).getPhotoData() :
                                                mListItem.getAvatarBytes())
                                        .build();
                            }

                            mAvatarView.setAvatar(avatarInfo);
                        }

                    } else {
                        if (listItem.getParticipants() != null && listItem.getParticipants().size() > 0) {
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            ((Activity) mContext).getWindowManager()
                                    .getDefaultDisplay()
                                    .getMetrics(displayMetrics);


                            Resources r = mContext.getResources();
                            float px = TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    AVATAR_VIEW_WIDTH_DP +
                                            PARTICIPANT_TEXTVIEW_MARGINS_DP +
                                            LIST_ITEM_PADDING_DP,
                                    r.getDisplayMetrics()
                            );

                            int width = displayMetrics.widthPixels - (int) px;
                            mTitleTextView.setText(StringUtil.getGroupChatParticipantsString(listItem.getParticipants(),
                                    width,
                                    mTitleTextView.getPaint(),
                                    mContext));

                        } else {
                            mTitleTextView.setText(mContext.getString(R.string.chats_group_chat_title));
                        }

                        if (mAvatarView != null) {
                            AvatarInfo avatarInfo = new AvatarInfo.Builder()
                                    .setIconResId(R.drawable.avatar_group)
                                    .build();

                            mAvatarView.setAvatar(avatarInfo);
                        }
                    }

                    //mRoomImageView.setVisibility(View.VISIBLE);
                    break;
                }
                case Enums.Chats.ConversationTypes.GROUP_ALIAS:
                    if (listItem.getData().getMembersList() != null && listItem.getData().getMembersList().size() > 0 && getUiNameList().size() > 0) {
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        ((Activity) mContext).getWindowManager()
                                .getDefaultDisplay()
                                .getMetrics(displayMetrics);


                        Resources r = mContext.getResources();
                        float px = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                AVATAR_VIEW_WIDTH_DP +
                                        PARTICIPANT_TEXTVIEW_MARGINS_DP +
                                        LIST_ITEM_PADDING_DP,
                                r.getDisplayMetrics()
                        );

                        int width = displayMetrics.widthPixels - (int) px;
                        mTitleTextView.setText(StringUtil.getGroupChatParticipantsString(getUiNameList(),
                                width,
                                mTitleTextView.getPaint(),
                                mContext));

                    } else {
                        mTitleTextView.setText(mListItem.getData().getMembers());
                    }

                    if (mAvatarView != null) {
                        AvatarInfo avatarInfo = new AvatarInfo.Builder()
                                .setIconResId(R.drawable.avatar_group)
                                .build();

                        mAvatarView.setAvatar(avatarInfo);
                    }
                    break;
                case Enums.Chats.ConversationTypes.GROUP_ALERT:
                case Enums.Chats.ConversationTypes.GROUP_BROADCAST: {
                    break;
                }
            }

            if (!TextUtils.isEmpty(mListItem.getData().getLastMessageBody())) {
                mSubtitleTextView.setText(mListItem.getData().getLastMessageBody());
            }

            if (!TextUtils.isEmpty(mListItem.getFormattedTimeString())) {
                mTimeTextView.setText(mListItem.getFormattedTimeString());
            } else {
                mTimeTextView.setText("");
            }
            updateUnreadMessageCount();


        } else {
            mTitleTextView.setText(mListItem.getData().getMembers());
        }

        setContentDescriptions();
    }

    private void updateUnreadMessageCount() {
        if (mListItem.getUnreadMessagesCount() > 0) {
            txtUnreadChatMessagesCount.setVisibility(View.VISIBLE);
            txtUnreadChatMessagesCount.setText(String.valueOf(mListItem.getUnreadMessagesCount()));
            mTimeTextView.setTextColor(mContext.getResources().getColor(R.color.nextivaPrimaryBlue));

        } else {
            txtUnreadChatMessagesCount.setVisibility(View.GONE);
            mTimeTextView.setTextColor(mContext.getResources().getColor(R.color.smsMessageListItemSubtitle));
        }
    }

    private ArrayList<String> getUiNameList() {
        ArrayList<String> uiNameList = new ArrayList<>();

        for (String jid : mListItem.getData().getMembersList()) {
            if (mSessionManager.getUserDetails() != null && !TextUtils.equals(jid, mSessionManager.getUserDetails().getImpId())) {
                uiNameList.add(mDbManager.getUINameFromJid(jid));
            }
        }

        return uiNameList;
    }

    private void setContentDescriptions() {
        mMasterItemView.setContentDescription(TextUtils.equals(mListItem.getData().getConversationType(), Enums.Chats.ConversationTypes.GROUP_CHAT) ?
                mContext.getString(R.string.chat_conversation_list_item_group_chat_content_description) :
                mContext.getString(R.string.chat_conversation_list_item_content_description, mTitleTextView.getText()));
        mTitleTextView.setContentDescription(mContext.getString(R.string.chat_conversation_list_item_name_content_description, mTitleTextView.getText()));
        mSubtitleTextView.setContentDescription(mContext.getString(R.string.chat_conversation_list_item_last_message_content_description, mTitleTextView.getText()));
        mTimeTextView.setContentDescription(mContext.getString(R.string.chat_conversation_list_item_datetime_content_description, mTitleTextView.getText()));
        mAvatarView.setContentDescription(mContext.getString(R.string.chat_conversation_list_item_avatar_content_description, mTitleTextView.getText()));
    }

    private void bindViews(View view) {
        ListItemChatConversationBinding binding = ListItemChatConversationBinding.bind(view);

        mAvatarView = binding.listItemChatConversationAvatarView;
        mTitleTextView = binding.listItemChatConversationTitleTextView;
        mSubtitleTextView = binding.listItemChatConversationSubTitleTextView;
        mTimeTextView = binding.listItemChatConversationTimeTextView;
        mRoomImageView = binding.listItemChatConversationRoomImageView;
        txtUnreadChatMessagesCount = binding.listItemUnreadChatCountTextView;
    }

    // --------------------------------------------------------------------------------------------
    // View.OnClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (mListItem != null && mMasterListListener != null) {
            mMasterListListener.onChatConversationItemClicked(mListItem);
        }
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // View.OnLongClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean onLongClick(View v) {
        if (mListItem != null && mMasterListListener != null) {
            mMasterListListener.onChatConversationItemLongClicked(mListItem);
        }

        return true;
    }
    // --------------------------------------------------------------------------------------------
}
