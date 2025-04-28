/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.listeners;

import com.karumi.dexter.listener.single.PermissionListener;

public abstract class NextivaPermissionListener implements PermissionListener {

    /**
     * In order to allow the correct flow when the user denies the permission dialog, we will store this value
     * as the permission is requested to keep track of what the value was before they are shown the dialog.
     * <br><br>
     * This allows us to only show the Denied dialog when the user had previously permanently
     * denied the permission request
     */
    protected boolean mShouldHaveShownRationaleDialog;


    public NextivaPermissionListener(boolean shouldHaveShownRationaleDialog) {
        mShouldHaveShownRationaleDialog = shouldHaveShownRationaleDialog;
    }
}
