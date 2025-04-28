package com.nextiva.nextivaapp.android.db.response

data class DatabaseResponse<T>(var isSuccess: Boolean,
                               var responseObject: T?)