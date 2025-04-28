package com.nextiva.nextivaapp.android.util.extensions

fun Boolean?.orFalse() : Boolean = this ?: false

fun Boolean?.orTrue() : Boolean = this ?: true