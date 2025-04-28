package com.nextiva.nextivaapp.android.util.extensions

import kotlin.math.abs

fun Int.dayWithSuffix(): String {
    if (this in 11..13) {
        return "${this}th"
    }

    return when (this % 10) {
        1 -> "${this}st"
        2 -> "${this}nd"
        3 -> "${this}rd"
        else -> "${this}th"
    }
}

fun Int?.orZero() : Int = this ?: 0

fun Int.toPositive(): Int = abs(this)

fun Long?.orZero() : Long = this ?: 0
