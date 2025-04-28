/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.mocks;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calls.BroadsoftCallDetails;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.CallHistoryResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.DeleteAllCallsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.DeleteCallResponseEvent;

import javax.inject.Inject;

import io.reactivex.Single;

public class FakeCallManagementRepository implements CallManagementRepository {

    @Inject
    public FakeCallManagementRepository() {
    }

    @Override
    public Single<BroadsoftCallDetails> getActiveCallInformation(String callId) {
        return Single.never();
    }

    @Override
    public Single<RxEvents.ResetConferenceCallResponseEvent> getResetConferenceCall() {
        return null;
    }

    @Override
    public Single<CallHistoryResponseEvent> getAllCallLogEntries() {
        return Single.just(new CallHistoryResponseEvent(false, null));
    }

    @Override
    public Single<RxEvents.ClearConferenceCallsResponseEvent> clearConferenceCall() {
        return null;
    }

    @Override
    public Single<RxEvents.IsExistingActiveCallResponseEvent> isExistingActiveCall() {
        return null;
    }

    @Override
    public Single<DeleteCallResponseEvent> deleteCall(@NonNull String callType, @NonNull String callId) {
        return Single.just(new DeleteCallResponseEvent(false, callType, callId));
    }

    @Override
    public void clearActiveCalls() {
    }

    @Override
    public Single<DeleteAllCallsResponseEvent> deleteAllCalls() {
        return Single.just(new DeleteAllCallsResponseEvent(false));
    }

    @Override
    public void rejectCall(String callId) {

    }

    @Override
    public Single<Integer> getActiveCallCount() {
        return Single.never();
    }
}
