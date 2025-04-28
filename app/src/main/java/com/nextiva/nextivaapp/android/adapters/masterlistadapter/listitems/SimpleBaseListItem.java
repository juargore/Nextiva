/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.NonNull;

/**
 * Created by adammacdonald on 2/8/18.
 */

public abstract class SimpleBaseListItem<T> extends BaseListItem {

    @NonNull
    private T mData;

    public SimpleBaseListItem(@NonNull T data) {
        this.mData = data;
    }

    @NonNull
    public T getData() {
        return mData;
    }

    public void setData(@NonNull T data) {
        this.mData = data;
    }
}
