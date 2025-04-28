/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.graphics.Typeface;
import android.telephony.PhoneNumberUtils;
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
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallHistoryListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.ListItemCallHistoryBinding;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.AvatarInfo;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;
import com.nextiva.nextivaapp.android.view.AvatarView;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by adammacdonald on 2/12/18.
 */

public class CallHistoryViewHolder extends BaseViewHolder<CallHistoryListItem> implements
        View.OnClickListener,
        View.OnLongClickListener {

    private final View mMasterItemView;
    protected AvatarView mAvatarView;
    protected TextView mTitleTextView;
    protected TextView mSubTitleTextView;
    protected TextView mDatetimeTextView;
    protected ImageView mCallTypeImageView;
    protected ImageView mCallImageView;

    @Inject
    protected SettingsManager mSettingsManager;

    public CallHistoryViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_call_history, parent, false),
             context,
             masterListListener);
    }

    private CallHistoryViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);
        bindViews(itemView);
        mMasterItemView = itemView;

        mMasterItemView.setOnClickListener(this);
        mMasterItemView.setOnLongClickListener(this);

        mCallImageView.setOnClickListener(this);
    }

    @Override
    public void bind(@NonNull CallHistoryListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;

        mMasterItemView.setLongClickable(mListItem.isListItemLongClickable());
        setContentDescriptions();

        if (!TextUtils.isEmpty(mListItem.getData().getCallType())) {
            switch (mListItem.getData().getCallType()) {
                case Enums.Calls.CallTypes.PLACED: {
                    mCallTypeImageView.setImageResource(R.drawable.ic_call_made);
                    mCallTypeImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.nextivaGreen));
                    mTitleTextView.setTextColor(ContextCompat.getColor(mContext, ApplicationUtil.isNightModeEnabled(mContext, mSettingsManager) ? R.color.white : R.color.black));
                    mTitleTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    mSubTitleTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    break;
                }
                case Enums.Calls.CallTypes.RECEIVED: {
                    mCallTypeImageView.setImageResource(R.drawable.ic_call_received);
                    mCallTypeImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.dkGrey));
                    mTitleTextView.setTextColor(ContextCompat.getColor(mContext, ApplicationUtil.isNightModeEnabled(mContext, mSettingsManager) ? R.color.white : R.color.black));
                    mTitleTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    mSubTitleTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    break;
                }
                case Enums.Calls.CallTypes.MISSED: {
                    mCallTypeImageView.setImageResource(R.drawable.ic_call_missed);
                    mCallTypeImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.errorRed));
                    mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.errorRed));
                    mTitleTextView.setTypeface(Typeface.defaultFromStyle(mListItem.getData().getIsRead() ? Typeface.NORMAL : Typeface.BOLD));
                    mSubTitleTextView.setTypeface(Typeface.defaultFromStyle(mListItem.getData().getIsRead() ? Typeface.NORMAL : Typeface.BOLD));
                    break;
                }
            }
        } else {
            mCallTypeImageView.setImageDrawable(null);
            mCallTypeImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
            mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }

        if (mListItem.isActionButtonVisible()) {
            mCallImageView.setVisibility(View.VISIBLE);

        } else {
            mCallImageView.setVisibility(View.GONE);
        }

        mTitleTextView.setText(mListItem.getData().getHumanReadableName());

        String subtitle = mListItem.getData().getPhoneNumber();

        if (!TextUtils.isEmpty(subtitle)) {
            mSubTitleTextView.setText(PhoneNumberUtils.formatNumber(subtitle, Locale.getDefault().getCountry()));
        }

        mDatetimeTextView.setText(mListItem.getFormattedDateTime());

        AvatarInfo avatarInfo = new AvatarInfo.Builder()
                .setPhotoData(mListItem.getData().getAvatar())
                .setPresence(new DbPresence(mListItem.getData().getJid(),
                        mListItem.getData().getPresenceState(),
                        mListItem.getData().getPresencePriority(),
                        mListItem.getData().getStatusText(),
                        mListItem.getData().getPresenceType()))
                .build();

        if (mListItem.getData().getAvatar() == null && mListItem.getData().getContactType() == Enums.Contacts.ContactTypes.CONFERENCE) {
            avatarInfo.setIconResId(R.drawable.ic_phone);

        } else if (!TextUtils.isEmpty(mListItem.getData().getUiName())) {
            avatarInfo.setDisplayName(mListItem.getData().getUiName());
        }

        mAvatarView.setAvatar(avatarInfo);
    }

    private void setContentDescriptions() {
        mMasterItemView.setContentDescription(mContext.getString(R.string.call_history_list_item_content_description,
                                                                 mListItem.getData().getCallType(),
                                                                 TextUtils.equals(mListItem.getData().getCallType(), Enums.Calls.CallTypes.PLACED) ?
                                                                         mContext.getString(R.string.call_history_list_to) : mContext.getString(R.string.call_history_list_from),
                                                                 mListItem.getData().getUiName(),
                                                                 mListItem.getFormattedDateTime()));
        mTitleTextView.setContentDescription(mContext.getString(R.string.call_history_list_item_name_content_description,
                                                                TextUtils.equals(mListItem.getData().getCallType(), Enums.Calls.CallTypes.PLACED) ?
                                                                        mContext.getString(R.string.call_history_list_callee) : mContext.getString(R.string.call_history_list_caller)));
        mSubTitleTextView.setContentDescription(mContext.getString(R.string.call_history_list_item_number_content_description,
                                                                   TextUtils.equals(mListItem.getData().getCallType(), Enums.Calls.CallTypes.PLACED) ?
                                                                           mContext.getString(R.string.call_history_list_callee) : mContext.getString(R.string.call_history_list_caller)));
        mDatetimeTextView.setContentDescription(mContext.getString(R.string.call_history_list_item_datetime_content_description,
                                                                   TextUtils.equals(mListItem.getData().getCallType(), Enums.Calls.CallTypes.PLACED) ?
                                                                           mContext.getString(R.string.call_history_list_callee) : mContext.getString(R.string.call_history_list_caller)));
        mAvatarView.setContentDescription(mContext.getString(R.string.call_history_list_item_avatar_content_description,
                                                             TextUtils.equals(mListItem.getData().getCallType(), Enums.Calls.CallTypes.PLACED) ?
                                                                     mContext.getString(R.string.call_history_list_callee) : mContext.getString(R.string.call_history_list_caller)));
        mCallTypeImageView.setContentDescription(mContext.getString(R.string.call_history_list_item_call_type_icon_content_description,
                                                                    mListItem.getData().getCallType()));
        mCallImageView.setContentDescription(mContext.getString(R.string.call_history_list_item_call_button_content_description,
                                                                TextUtils.equals(mListItem.getData().getCallType(), Enums.Calls.CallTypes.PLACED) ?
                                                                        mContext.getString(R.string.call_history_list_callee) : mContext.getString(R.string.call_history_list_caller)));
    }

    private void bindViews(View view) {
        ListItemCallHistoryBinding binding = ListItemCallHistoryBinding.bind(view);

        mAvatarView = binding.listItemCallHistoryAvatarView;
        mTitleTextView = binding.listItemCallHistoryTitleTextView;
        mSubTitleTextView = binding.listItemCallHistorySubTitleTextView;
        mDatetimeTextView = binding.listItemCallHistoryDatetimeTextView;
        mCallTypeImageView = binding.listItemCallHistoryCallTypeImageView;
        mCallImageView = binding.listItemCallHistoryCallImageView;
    }

    // --------------------------------------------------------------------------------------------
    // View.OnClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            if (v.getId() == mCallImageView.getId()) {
                mMasterListListener.onCallHistoryCallButtonClicked(mListItem);

            } else {
                mMasterListListener.onCallHistoryListItemClicked(mListItem);
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
            mMasterListListener.onCallHistoryListItemLongClicked(mListItem);
        }

        return true;
    }
    // --------------------------------------------------------------------------------------------
}
