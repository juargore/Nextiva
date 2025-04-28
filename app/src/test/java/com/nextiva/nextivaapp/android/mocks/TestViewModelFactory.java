/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.mocks;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.powermock.api.mockito.PowerMockito;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TestViewModelFactory implements ViewModelProvider.Factory {

    @Inject
    public TestViewModelFactory() {
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return PowerMockito.mock(modelClass);
    }
}
