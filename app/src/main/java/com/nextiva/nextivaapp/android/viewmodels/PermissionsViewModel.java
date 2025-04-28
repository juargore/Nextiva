/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nextiva.nextivaapp.android.models.PermissionRequest;

/**
 * Created by Thaddeus Dannar on 6/11/18.
 */
public class PermissionsViewModel extends ViewModel {

    private MutableLiveData<PermissionRequest> mPermissionRequestMutableLiveData;


    public PermissionsViewModel() {
    }

    public MutableLiveData<PermissionRequest> getPermissionRequestMutableLiveData() {
        if (mPermissionRequestMutableLiveData == null) {
            mPermissionRequestMutableLiveData = new MutableLiveData<>();
        }
        return mPermissionRequestMutableLiveData;
    }

}
