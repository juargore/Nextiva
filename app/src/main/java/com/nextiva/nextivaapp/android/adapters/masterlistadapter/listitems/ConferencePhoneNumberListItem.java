/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;

/**
 * Created by joedephillipo on 2/22/18.
 */

public class ConferencePhoneNumberListItem extends DetailItemViewListItem {

    @Enums.Contacts.DetailViewTypes.Type
    private final int mViewType;
    private final String mConferenceId;
    private final String mSecurityPin;
    private final String mAssembledConferencePhoneNumber;

    public ConferencePhoneNumberListItem(
            @Enums.Contacts.DetailViewTypes.Type int viewType,
            @NonNull String title,
            @Nullable String subTitle,
            @Nullable String conferenceId,
            @Nullable String securityPin,
            @Nullable String assembledConferencePhoneNumber) {

        super(title, subTitle, R.drawable.ic_phone, R.drawable.ic_video, false, true);
        mViewType = viewType;
        mConferenceId = conferenceId;
        mSecurityPin = securityPin;
        mAssembledConferencePhoneNumber = assembledConferencePhoneNumber;
    }

    @Enums.Contacts.DetailViewTypes.Type
    public int getViewType() {
        return mViewType;
    }


    public String getConferenceId() {
        return mConferenceId;
    }


    public String getSecurityPin() {
        return mSecurityPin;
    }

    public String getAssembledConferencePhoneNumber() {
        return mAssembledConferencePhoneNumber;
    }

}
