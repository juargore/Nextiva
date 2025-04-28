package com.nextiva.nextivaapp.android.models

import com.nextiva.nextivaapp.android.constants.Enums

class Resource<T> private constructor(@Enums.Net.StatusTypes.StatusType val status: String,
                                      val data: T?,
                                      val message: String?) {

    companion object {
        @JvmStatic
        fun <T> success(data: T): Resource<T> {
            return Resource(Enums.Net.StatusTypes.SUCCESS, data, null)
        }

        @JvmStatic
        fun <T> error(message: String, data: T?): Resource<T> {
            return Resource(Enums.Net.StatusTypes.ERROR, data, message)
        }

        @JvmStatic
        fun <T> loading(data: T?): Resource<T> {
            return Resource(Enums.Net.StatusTypes.LOADING, data, null)
        }
    }
}