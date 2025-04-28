/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.rx;

import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NextivaSchedulerProvider implements SchedulerProvider {

    @Inject
    public NextivaSchedulerProvider() {
    }

    // --------------------------------------------------------------------------------------------
    // SchedulerProvider Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @Override
    public Scheduler newThread() {
        return Schedulers.newThread();
    }
    // --------------------------------------------------------------------------------------------
}
