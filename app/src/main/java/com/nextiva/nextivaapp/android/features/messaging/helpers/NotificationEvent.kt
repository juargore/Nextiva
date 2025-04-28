package com.nextiva.nextivaapp.android.features.messaging.helpers

data class NotificationEvent<T>(
    val status: T,
    val event: Event,
) {
    enum class Event {
        SINGLE_DELETE,
        BULK_DELETE,
        SINGLE_READ_STATUS,
        BULK_READ_STATUS
    }
}