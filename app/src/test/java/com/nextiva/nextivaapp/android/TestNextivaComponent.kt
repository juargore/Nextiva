/*
 * Copyright (c) 2024. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android

import android.app.Application
import com.google.android.datatransport.runtime.dagger.BindsInstance
import com.google.android.datatransport.runtime.dagger.Component
import com.nextiva.nextivaapp.android.di.modules.TestAppModule
import javax.inject.Singleton

/**
 * Created by Thaddeus Dannar on 6/27/24.
 */
@Singleton
@Component(modules = [TestAppModule::class])
interface TestNextivaComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): TestNextivaComponent
    }

    //fun inject(test: AddEditContactViewModelTest)
}