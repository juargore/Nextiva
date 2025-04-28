/*
 * Copyright (c) 2022. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings.models.net.events

import com.nextiva.nextivaapp.android.models.net.mediacall.RepeatOnItem


data class Recurrence(
    val cancelledEvents: Any? = null,
    val repeatOnMonths: Any? = null,
    val repeatBySetPos: Any? = null,
    val recurrenceEndBy: Any? = null,
    val pattern: String? = null,
    val repeatOnDaysOfMonth: Any? = null,
    val interval: Int? = null,
    val repeatUntil: Any? = null,
    val repeatOn: List<RepeatOnItem?>? = null,
    val repeatCount: Int? = null,
    val repeatWkst: Any? = null,
)