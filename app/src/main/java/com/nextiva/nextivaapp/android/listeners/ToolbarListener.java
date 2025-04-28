/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.listeners;

import androidx.annotation.StringRes;

/**
 * Created by adammacdonald on 2/20/18.
 */

public interface ToolbarListener {

    void setToolbarTitle(String title);

    void setToolbarTitle(@StringRes int titleResId);

    void setToolbarElevation(float elevation);
}
