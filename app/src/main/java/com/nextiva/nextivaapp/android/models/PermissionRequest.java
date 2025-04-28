/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

/**
 * Created by Thaddeus Dannar on 6/11/18.
 */
public class PermissionRequest {

    private final String[] mPermissions;
    private final Integer mResultsCode;

    public PermissionRequest(final String[] permissions, final Integer resultsCode) {
        mPermissions = permissions;
        mResultsCode = resultsCode;
    }

    public String[] getPermissions() {
        return mPermissions;
    }

    public Integer getResultsCode() {
        return mResultsCode;
    }
}
