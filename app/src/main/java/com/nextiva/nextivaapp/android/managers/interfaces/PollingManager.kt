/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android.managers.interfaces

import kotlinx.coroutines.CoroutineScope

interface PollingManager {

    fun stopPolling()

    fun startPolling(scope: CoroutineScope)
}
