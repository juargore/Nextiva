/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import androidx.annotation.Nullable;

public class DbResponse<T> {

    @Nullable
    private final T mValue;

    public DbResponse(@Nullable T value) {
        mValue = value;
    }

    @Nullable
    public T getValue() {
        return mValue;
    }
}
