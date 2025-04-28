/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.apimanagers.repositories;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calls.BroadsoftCallDetails;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.CallHistoryResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.DeleteAllCallsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.DeleteCallResponseEvent;

import io.reactivex.Single;

/**
 * Created by adammacdonald on 2/14/18.
 * <p>
 * Repository used to get/update data associated to the currently logged in user's calls
 */

public interface CallManagementRepository {

    Single<CallHistoryResponseEvent> getAllCallLogEntries();

    Single<RxEvents.ResetConferenceCallResponseEvent> getResetConferenceCall();

    Single<RxEvents.ClearConferenceCallsResponseEvent> clearConferenceCall();

    Single<RxEvents.IsExistingActiveCallResponseEvent> isExistingActiveCall();

    Single<BroadsoftCallDetails> getActiveCallInformation(String callId);

    void clearActiveCalls();

    Single<Integer> getActiveCallCount();

    Single<DeleteCallResponseEvent> deleteCall(
            @NonNull @Enums.Calls.CallTypes.Type String callType,
            @NonNull String callId);

    Single<DeleteAllCallsResponseEvent> deleteAllCalls();

    void rejectCall(String callId);

}
