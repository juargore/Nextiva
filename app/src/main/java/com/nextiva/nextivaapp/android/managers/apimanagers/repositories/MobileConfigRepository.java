/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.apimanagers.repositories;

import com.nextiva.nextivaapp.android.net.buses.RxEvents.MobileConfigResponseEvent;

import io.reactivex.Single;

/**
 * Created by joedephillipo on 2/21/18.
 */

public interface MobileConfigRepository {
    Single<MobileConfigResponseEvent> getMobileConfig();
}
