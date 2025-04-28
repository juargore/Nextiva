/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.modules.junit4.PowerMockRunner


@RunWith(PowerMockRunner::class)
class DbResponseTest {

    @Test
    fun getValue_returnsCorrectValue() {
        val dbResponse = DbResponse("String")

        assertEquals("String", dbResponse.value)
    }
}