/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.models.NextivaContact;

import io.reactivex.disposables.CompositeDisposable;

public interface ContactManager {

    void getContactByPhoneNumberFromApi(@NonNull final String phoneNumber,
                                        @Enums.Sip.CallTypes.Type final int callType,
                                        @NonNull CompositeDisposable compositeDisposable,
                                        @NonNull GetContactCallback nextivaContactCallback);

    interface GetContactCallback {
        void onNextivaContactReturned(NextivaContact nextivaContact,
                                      @NonNull String phoneNumber,
                                      @Enums.Sip.CallTypes.Type int callType);

        void onFailure(@NonNull String phoneNumber, int callType);
    }
}
