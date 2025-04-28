/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.di.modules;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nextiva.nextivaapp.android.di.annotations.ViewModelKey;
import com.nextiva.nextivaapp.android.viewmodels.ViewModelFactoryModule;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;
import dagger.multibindings.IntoMap;

@Module
@TestInstallIn(
        components = SingletonComponent.class,
        replaces = ViewModelFactoryModule.class
)
public abstract class TestViewModelModule {

    @Provides
    @Singleton
    public static ViewModelProvider.Factory providesViewModelFactory() {
        return Mockito.mock(ViewModelProvider.Factory.class);
    }

}
