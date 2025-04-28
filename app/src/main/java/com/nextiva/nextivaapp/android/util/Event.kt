package com.nextiva.nextivaapp.android.util

import androidx.lifecycle.Observer as LifecycleObserver

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T?  = if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }

    fun peekContent(): T = content

    class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) :
        LifecycleObserver<Event<T>> {
        override fun onChanged(event: Event<T>) {
            event.getContentIfNotHandled()?.let { value ->
                onEventUnhandledContent(value)
            }
        }
    }
}