package com.nextiva.nextivaapp.android.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SingleEvent<T> {

    @NonNull
    private final T mContent;

    private boolean mHasBeenHandled = false;

    public SingleEvent(@NonNull T content) {
        mContent = content;
    }

    /**
     * Returns the content but only if this is the first time it is being requested.  Any
     * subsequent calls will return null, as the content would have been consumed upon the
     * first request to get it.
     *
     * @return the content if it had not already been handled
     */
    @Nullable
    public T getContentIfNotHandled() {
        if (!mHasBeenHandled) {
            mHasBeenHandled = true;
            return mContent;

        } else {
            return null;
        }
    }

    /**
     * Returns the content even if it had already been consumed.
     *
     * @return the content even if it had been previously consumed
     */
    @NonNull
    public T peekContent() {
        return mContent;
    }
}
