/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import org.jetbrains.annotations.Nullable;

/**
 * Created by adammacdonald on 2/8/18.
 */

public abstract class BaseListItem {
    @Nullable
    public Boolean forceChangeState;

    protected BaseListItem() {
        this.forceChangeState = false;
    }
}
