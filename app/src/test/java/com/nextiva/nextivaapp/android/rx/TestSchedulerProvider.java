/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.rx;

import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class TestSchedulerProvider implements SchedulerProvider {

    @Inject
    public TestSchedulerProvider() {
    }

    // --------------------------------------------------------------------------------------------
    // SchedulerProvider Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Scheduler ui() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler newThread() {
        return Schedulers.trampoline();
    }
    // --------------------------------------------------------------------------------------------
}
