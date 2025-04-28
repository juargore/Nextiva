/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.ListItemContactBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.AvatarInfo;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.nextivaapp.android.view.AvatarView;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by adammacdonald on 2/9/18.
 */

public class ContactViewHolder extends BaseViewHolder<ContactListItem> implements
        View.OnClickListener,
        View.OnLongClickListener {

    private final View mMasterItemView;
    protected AvatarView mAvatarView;
    protected TextView mTitleTextView;
    protected TextView mSubTitleTextView;

    @Inject
    protected AvatarManager mAvatarManager;
    @Inject
    protected SessionManager mSessionManager;
    @Inject
    protected SettingsManager mSettingsManager;

    private final Context mContext;

    @Inject
    public ContactViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false),
                context,
                masterListListener);
    }

    private ContactViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);
        bindViews(itemView);
        mMasterItemView = itemView;

        mContext = context;
        mMasterItemView.setOnClickListener(this);
        mMasterItemView.setOnLongClickListener(this);
    }

    @Override
    public void bind(@NonNull ContactListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;

        mMasterItemView.setLongClickable(mListItem.isListItemLongClickable());
        setContentDescriptions();

        boolean searchMatchSet = false;

        try {
            if (mSessionManager != null && mSessionManager.getUserDetails() != null && !TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId()) && !TextUtils.equals(mListItem.getData().getJid(), mSessionManager.getUserDetails().getImpId())) {
                mAvatarView.setAvatar(mListItem.getData().getAvatarInfo());

            } else {
                AvatarInfo avatarInfo = mListItem.getData().getAvatarInfo();
                avatarInfo.setPhotoData(mSessionManager.getOwnAvatar());
                avatarInfo.setPresence(mSessionManager.getUserPresence());

                mAvatarView.setAvatar(avatarInfo);
            }
        } catch (Exception e) {
            LogUtil.e(e.toString());
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        mAvatarView.setState(mContext, listItem.getAvatarState(), ApplicationUtil.isNightModeEnabled(mContext, mSettingsManager));
        String uiName = mListItem.getData().getUiName();

        if (!TextUtils.isEmpty(mListItem.getSearchTerm()) &&
                !TextUtils.isEmpty(uiName) &&
                uiName.toLowerCase().contains(mListItem.getSearchTerm().toLowerCase())) {

            // Search matched off the contact's name
            searchMatchSet = true;
        }

        mTitleTextView.setText(uiName);

        if (!TextUtils.isEmpty(mListItem.getmConversationType()) && mListItem.getmConversationType().equalsIgnoreCase(Constants.MESSAGE_CONVERSATION_TYPE)) {
            if (TextUtils.isEmpty(mListItem.getData().getJid()) && TextUtils.isEmpty(mListItem.getData().getUserId())){
                if (!TextUtils.isEmpty(uiName)){
                    mTitleTextView.setText("Send to "+ uiName);
                }else {
                    if (mListItem.getData().getAllPhoneNumbers()!=null && mListItem.getData().getAllPhoneNumbers().size()>0){
                        mTitleTextView.setText("Send to "+ mListItem.getData().getAllPhoneNumbers().get(0).getStrippedNumber());
                    }
                }

            }
            if (mListItem != null && mListItem.getData().getAllPhoneNumbers() != null && mListItem.getData().getAllPhoneNumbers().size() > 0 && !TextUtils.isEmpty(mListItem.getData().getAllPhoneNumbers().get(0).getNumber()))
                mSubTitleTextView.setText(PhoneNumberUtils.formatNumber(mListItem.getData().getAllPhoneNumbers().get(0).getStrippedNumber(), Locale.getDefault().getCountry()));
        } else {
            if (!TextUtils.isEmpty(mListItem.getSearchTerm()) && !searchMatchSet) {
                mSubTitleTextView.setText(mListItem.getData().getSearchMatchText(mListItem.getSearchTerm().toLowerCase()));
                mSubTitleTextView.setVisibility(View.VISIBLE);

            } else {
                mSubTitleTextView.setText(null);
                mSubTitleTextView.setVisibility(View.GONE);
            }
        }

        if (mListItem.getData().getContactType() == Enums.Contacts.ContactTypes.PERSONAL) {
            if (mListItem.getData().getPresence() != null || mListItem.getData().getSubscriptionState() == Enums.Contacts.SubscriptionStates.PENDING) {
                if (!TextUtils.isEmpty(mListItem.getData().getHumanReadablePresenceText()) && mListItem.getData().getSubscriptionState() != Enums.Contacts.SubscriptionStates.UNSUBSCRIBED) {
                    mSubTitleTextView.setText(mListItem.getData().getHumanReadablePresenceText());
                    mSubTitleTextView.setVisibility(View.VISIBLE);

                } else {
                    mSubTitleTextView.setText(null);
                    mSubTitleTextView.setVisibility(View.GONE);
                }

            } else {
                if (TextUtils.isEmpty(mSubTitleTextView.getText())) {
                    mSubTitleTextView.setText(null);
                    mSubTitleTextView.setVisibility(View.GONE);
                }
            }

        } else {
            if (TextUtils.isEmpty(mSubTitleTextView.getText())) {
                mSubTitleTextView.setText(null);
                mSubTitleTextView.setVisibility(View.GONE);
            }
        }
    }

    private void setContentDescriptions() {
        mMasterItemView.setContentDescription(mContext.getString(R.string.contact_list_item_content_description, mListItem.getData().getUiName()));
        mTitleTextView.setContentDescription(mContext.getString(R.string.contact_list_item_name_content_description, mListItem.getData().getUiName()));
        mSubTitleTextView.setContentDescription(mContext.getString(R.string.contact_list_item_status_content_description, mListItem.getData().getUiName()));
        mAvatarView.setContentDescription(mContext.getString(R.string.contact_list_item_avatar_content_description, mListItem.getData().getUiName()));
    }

    private void bindViews(View view) {
        ListItemContactBinding binding = ListItemContactBinding.bind(view);

        mAvatarView = binding.listItemContactAvatarView;
        mTitleTextView = binding.listItemContactTitleTextView;
        mSubTitleTextView = binding.listItemContactSubTitleTextView;
    }

    public void clear() {
        mTitleTextView.setText(null);
        mSubTitleTextView.setText(null);
    }

    // --------------------------------------------------------------------------------------------
    // View.OnClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            mMasterListListener.onContactListItemClicked(mListItem);
        }
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // View.OnLongClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean onLongClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            mMasterListListener.onContactListItemLongClicked(mListItem);
        }

        return true;
    }
    // --------------------------------------------------------------------------------------------
}