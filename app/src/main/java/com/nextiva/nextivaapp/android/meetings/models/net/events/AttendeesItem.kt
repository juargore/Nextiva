/*
 * Copyright (c) 2022. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.events


data class AttendeesItem(
    val firstName: String? = null,
    val lastName: String? = null,
    val response: String? = null,
    val optional: Boolean? = null,
    val comment: String? = null,
    var type: String? = null,
    val responseToken: String? = null,
    val userId: String? = null,
    val email: String? = null,
    val status: Any? = null,
)