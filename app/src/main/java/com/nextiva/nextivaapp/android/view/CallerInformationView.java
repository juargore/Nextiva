/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.databinding.ViewCallerInformationBinding;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.models.AvatarInfo;
import com.nextiva.nextivaapp.android.models.CallInfo;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Thaddeus Dannar on 5/7/18.
 */
public class CallerInformationView extends CoordinatorLayout {

    protected TextView mCallerNameTextView;
    protected AvatarView mAvatarView;
    protected TextView mCallerNumberTextView;
    protected TextView mStatusTextView;
    protected TextView mNumberOfParticipantsTextView;
    protected ImageView mCallConnectedImageView;

    @SuppressWarnings("unused")
    private NextivaContact mContact;
    private String mPhoneNumber;
    private String mDisplayName;
    private int mCalleeCount = 1;
    private Context mContext;


    public CallerInformationView(@NonNull Context context) {
        this(context, null);
    }

    public CallerInformationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CallerInformationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mContext = context;
        bindViews(inflater, this);
    }

    private void bindViews(LayoutInflater inflater, ViewGroup container) {
        ViewCallerInformationBinding binding = ViewCallerInformationBinding.inflate(inflater, container, true);

        mCallerNameTextView = binding.callerInformationCallerNameTextView;
        mAvatarView = binding.callerInformationCallerAvatarView;
        mCallerNumberTextView = binding.callerInformationCallerNumberTextView;
        mStatusTextView = binding.callerInformationStatusTextView;
        mNumberOfParticipantsTextView = binding.callerInformationNumberOfParticipants;
        mCallConnectedImageView = binding.callerInformationCallConnectedImageView;
    }

    public void populateCallerInfo(@NonNull ArrayList<CallInfo> callInfoArrayList,
                                   @NonNull DbManager dbManager,
                                   @NonNull CompositeDisposable compositeDisposable,
                                   @NonNull Boolean isConference,
                                   @NonNull boolean isNextivaConnectEnabled) {

        StringBuilder callNameStringBuilder = new StringBuilder();

        mCalleeCount = callInfoArrayList.size();

        if (callInfoArrayList.size() > 1 || isConference) {
            mNumberOfParticipantsTextView.setText(getContext().getString(R.string.active_call_conference_callee_count, mCalleeCount));
            mNumberOfParticipantsTextView.setVisibility(VISIBLE);
        } else {
            mNumberOfParticipantsTextView.setVisibility(GONE);
        }

        for (CallInfo callInfo : callInfoArrayList) {
            if (callNameStringBuilder.length() > 0) {
                callNameStringBuilder.append(", ");
            }

            if (!TextUtils.isEmpty(callInfo.getDisplayName())) {
                callNameStringBuilder.append(callInfo.getDisplayName());
            } else if (callInfo.getNextivaContact() != null && !TextUtils.isEmpty(callInfo.getNextivaContact().getDisplayName())) {
                callNameStringBuilder.append(callInfo.getDisplayName());
            } else if (callInfo.getNextivaContact() != null && !TextUtils.isEmpty(callInfo.getNextivaContact().getFirstName())) {
                callNameStringBuilder.append(callInfo.getNextivaContact().getFirstName());
                if (!TextUtils.isEmpty(callInfo.getNextivaContact().getLastName())) {
                    callNameStringBuilder.append(" ");
                    callNameStringBuilder.append(callInfo.getNextivaContact().getLastName());
                }
            } else if (callInfo.getNextivaContact() != null && !TextUtils.isEmpty(callInfo.getNextivaContact().getLastName())) {
                callNameStringBuilder.append(callInfo.getNextivaContact().getLastName());
            } else {
                callNameStringBuilder.append(callInfo.getNumberToCall());
            }


        }
        mDisplayName = callNameStringBuilder.toString();


        if (callInfoArrayList.size() > 1 || isConference) {
            mPhoneNumber = getContext().getString(R.string.active_call_conference);
        } else {
            mPhoneNumber = null;
            if (callInfoArrayList.size() != 0 && callInfoArrayList.get(0) != null) {
                CallInfo firstCallInfo = callInfoArrayList.get(0);
                if (!TextUtils.isEmpty(firstCallInfo.getNumberToCall())) {
                    String[] seperatePhoneNumberFromDTMFTonesArray = CallUtil.separatePhoneNumberFromDTMFTones(firstCallInfo.getNumberToCall());
                    if (seperatePhoneNumberFromDTMFTonesArray.length > 0) {
                        mPhoneNumber = seperatePhoneNumberFromDTMFTonesArray[0];
                    }
                }
            }
            mDisplayName = callInfoArrayList.get(0).getDisplayName();
        }

        populateCallerInfo(dbManager, compositeDisposable, isNextivaConnectEnabled);
    }

    private void populateCallerInfo(@NonNull DbManager dbManager,
                                    @NonNull CompositeDisposable compositeDisposable,
                                    @NonNull Boolean isNextivaConnectEnabled) {

        if (mContact != null && !TextUtils.isEmpty(mContact.getUiName())) {
            mCallerNameTextView.setText(!TextUtils.isEmpty(mDisplayName) ? mDisplayName : mContact.getUiName());

            if (!CallUtil.getStrippedPhoneNumber(mContact.getUiName()).contains(mPhoneNumber)) {
                mCallerNumberTextView.setText(mPhoneNumber);
            } else {
                mCallerNumberTextView.setText("");
            }

            if (mAvatarView != null) {
                if (!TextUtils.isEmpty(mContact.getJid())) {
                    compositeDisposable.add(dbManager.setAvatar(mAvatarView, mContact.getJid()));
                }

                if (mCalleeCount < 2) {
                    mAvatarView.setAvatar(mContact.getAvatarInfo());
                } else {

                    AvatarInfo avatarInfo = new AvatarInfo.Builder()
                            .setIconResId(R.drawable.avatar_group)
                            .build();

                    mAvatarView.setAvatar(avatarInfo);
                }

            }

        } else if (!TextUtils.isEmpty(mDisplayName)) {

            if (mDisplayName != null &&
                    mPhoneNumber != null &&
                    !CallUtil.getStrippedPhoneNumber(mDisplayName).contains(mPhoneNumber)) {
                mCallerNumberTextView.setText(mPhoneNumber);
            } else {
                mCallerNumberTextView.setText("");
            }

            if (mCalleeCount < 2) {
                if (mAvatarView != null) {
                    AvatarInfo avatarInfo = null;

                    if (isNextivaConnectEnabled &&
                            dbManager.getConnectContactFromPhoneNumberInThread(mPhoneNumber) != null &&
                            dbManager.getConnectContactFromPhoneNumberInThread(mPhoneNumber).getValue() != null)
                    {
                        NextivaContact contactFromPhoneNumber = dbManager.getConnectContactFromPhoneNumberInThread(mPhoneNumber).getValue();
                        avatarInfo = contactFromPhoneNumber != null && contactFromPhoneNumber.getAvatarInfo() != null ?
                                        contactFromPhoneNumber.getAvatarInfo() : new AvatarInfo.Builder().setDisplayName(mDisplayName).build();
                    }

                    if (avatarInfo == null && dbManager.getContactFromPhoneNumberInThread(mPhoneNumber) != null &&
                            dbManager.getContactFromPhoneNumberInThread(mPhoneNumber).getValue() != null) {
                        NextivaContact contactFromPhoneNumber = dbManager.getContactFromPhoneNumberInThread(mPhoneNumber).getValue();

                        avatarInfo = contactFromPhoneNumber != null &&
                                contactFromPhoneNumber.getAvatarInfo() != null ?
                                contactFromPhoneNumber.getAvatarInfo() : new AvatarInfo.Builder().setDisplayName(mDisplayName).build();
                    }

                    if(avatarInfo == null) {
                        avatarInfo = new AvatarInfo.Builder().setDisplayName(mDisplayName).build();
                    }

                    mAvatarView.setAvatar(avatarInfo);

                }
            } else {

                AvatarInfo avatarInfo = new AvatarInfo.Builder()
                        .setIconResId(R.drawable.avatar_group)
                        .build();

                mAvatarView.setAvatar(avatarInfo);
            }

        } else {

            if(mPhoneNumber != null)
                mCallerNameTextView.setText(mPhoneNumber);
            else
                mCallerNameTextView.setText("");

            mCallerNumberTextView.setText("");

            if (mCalleeCount < 2) {
                if (mAvatarView != null) {
                    mAvatarView.setAvatar(new AvatarInfo.Builder().build());
                }
            } else {

                AvatarInfo avatarInfo = new AvatarInfo.Builder()
                        .setIconResId(R.drawable.avatar_group)
                        .build();

                mAvatarView.setAvatar(avatarInfo);
            }
        }

        setContentDescriptions();
    }

    public void setCallStatus(String callStatus) {
        mStatusTextView.setText(callStatus);
        mStatusTextView.setContentDescription(mContext.getString(R.string.caller_information_status_text_view_content_description));
    }

    public void setCallStatus(int callStatus) {
        mStatusTextView.setText(callStatus);
        mStatusTextView.setContentDescription(mContext.getString(R.string.caller_information_status_text_view_content_description));
    }

    private void setContentDescriptions() {
        mCallerNameTextView.setContentDescription(mContext.getString(R.string.caller_information_caller_name_text_view_content_description));
        mAvatarView.setContentDescription(mContext.getString(R.string.caller_information_caller_avatar_view_content_description));
        mCallerNumberTextView.setContentDescription(mContext.getString(R.string.caller_information_caller_number_text_view_content_description));
        mStatusTextView.setContentDescription(mContext.getString(R.string.caller_information_status_text_view_content_description));
        mNumberOfParticipantsTextView.setContentDescription(mContext.getString(R.string.caller_information_number_of_participants_text_view_content_description));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //TODO:Causing layout not to display on kit kat
        //TODO:Causing issues displaying layout
        if (!isInEditMode()) {
            mCallerNameTextView.addTextChangedListener(new ExtensionEnabledPhoneNumberFormattingTextWatcher());
            mCallerNumberTextView.addTextChangedListener(new ExtensionEnabledPhoneNumberFormattingTextWatcher());
        }
    }
}
