package com.nextiva.nextivaapp.android.models;

import androidx.annotation.Nullable;

/**
 * Created by Thaddeus Dannar on 11/6/20.
 */

public class BroadsoftCallCenterMainModel {

    @Nullable
    public CallCenter CallCenter;


    public BroadsoftCallCenterMainModel() {
    }
    public BroadsoftCallCenterMainModel(CallCenter broadsoftCallCenter) {
        CallCenter = broadsoftCallCenter;
    }

    @Nullable
    public CallCenter getBroadsoftCallCenter() {
        return CallCenter;
    }

    public void setBroadsoftCallCenter(@Nullable final CallCenter broadsoftCallCenter) {
        CallCenter = broadsoftCallCenter;
    }
}
