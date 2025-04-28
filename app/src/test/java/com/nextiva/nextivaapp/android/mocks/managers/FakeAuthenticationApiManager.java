/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.mocks.managers;

import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.AuthenticationRepository;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.AuthenticationResponseEvent;

import io.reactivex.Single;

/**
 * Created by adammacdonald on 2/28/18.
 */

public class FakeAuthenticationApiManager implements AuthenticationRepository {

    private boolean mAuthenticateUserSuccessful = true;

    public void setAuthenticateUserSuccessful(boolean authenticateUserSuccessful) {
        mAuthenticateUserSuccessful = authenticateUserSuccessful;
    }

    // --------------------------------------------------------------------------------------------
    // AuthenticationRepository Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Single<AuthenticationResponseEvent> authenticateUser(String username, String password) {
        return Single.just(new AuthenticationResponseEvent(mAuthenticateUserSuccessful, true));
    }

    @Override
    public Single<AuthenticationResponseEvent> getAuthenticationResponseEvent(String username) {
        return null;
    }

    @Override
    public Single<Boolean> getPollingDeviceSettings(String username) {
        return null;
    }
    // --------------------------------------------------------------------------------------------
}
