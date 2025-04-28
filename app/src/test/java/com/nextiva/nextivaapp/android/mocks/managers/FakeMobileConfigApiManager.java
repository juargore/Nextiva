/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.mocks.managers;

import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.MobileConfigRepository;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.MobileConfigResponseEvent;

import io.reactivex.Single;

/**
 * Created by adammacdonald on 2/9/18.
 */

public class FakeMobileConfigApiManager implements MobileConfigRepository {

    private boolean mMobileConfigSuccessful = true;

    public FakeMobileConfigApiManager() {
    }

    public void setMobileConfigSuccessful(boolean mobileConfigSuccessful) {
        mMobileConfigSuccessful = mobileConfigSuccessful;
    }

    // --------------------------------------------------------------------------------------------
    // MobileConfigRepository Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Single<MobileConfigResponseEvent> getMobileConfig() {
        return Single.just(new MobileConfigResponseEvent(mMobileConfigSuccessful));
    }
    // --------------------------------------------------------------------------------------------
}
