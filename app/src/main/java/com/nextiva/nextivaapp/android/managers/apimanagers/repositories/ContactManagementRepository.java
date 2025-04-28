/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.apimanagers.repositories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.EnterpriseContactByImpIdResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.EnterpriseContactByNameResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.EnterpriseContactByNumberResponseEvent;

import io.reactivex.Single;

/**
 * Created by joedephillipo on 2/15/18.
 * <p>
 * Repository used to get/update data associated to contacts on the server
 */

public interface ContactManagementRepository {

    Single<EnterpriseContactByImpIdResponseEvent> getEnterpriseContactByImpId(
            @NonNull String impId,
            @Enums.Sip.CallTypes.Type @Nullable Integer callType);

    Single<EnterpriseContactByNameResponseEvent> getEnterpriseContactByName(
            String name);

    Single<EnterpriseContactByNumberResponseEvent> getEnterpriseContactByPhoneNumber(
            @NonNull String phoneNumber,
            @NonNull @Enums.Sip.CallTypes.Type final Integer callType);

    Single<EnterpriseContactByNumberResponseEvent> getEnterpriseContactByExtension(
            @NonNull String extension,
            @NonNull @Enums.Sip.CallTypes.Type final Integer callType);

    @NonNull
    Single<Boolean> getEnterpriseContacts(boolean forceRefresh);
}
