/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.apimanagers.repositories;

import com.nextiva.nextivaapp.android.net.buses.RxEvents.AuthenticationResponseEvent;

import io.reactivex.Single;

/**
 * Created by adammacdonald on 2/2/18.
 */

public interface AuthenticationRepository {

    /**
     * Authenticate the user against the underlying repository.  Subscribe to
     * the {@link AuthenticationResponseEvent} event to process the returns from this call
     *
     * @param username The username of the user to authenticate
     * @param password The password of the user to authenticate
     */
    Single<AuthenticationResponseEvent> authenticateUser(String username, String password);

    Single<AuthenticationResponseEvent> getAuthenticationResponseEvent(final String username);

    Single<Boolean> getPollingDeviceSettings(final String username);
}
