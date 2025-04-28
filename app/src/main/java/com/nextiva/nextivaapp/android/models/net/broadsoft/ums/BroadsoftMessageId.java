package com.nextiva.nextivaapp.android.models.net.broadsoft.ums;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BroadsoftMessageId implements Serializable {

    @NonNull
    @SerializedName("msgid")
    private String mMessageId;

    public BroadsoftMessageId(@NonNull String messageId) {
        mMessageId = messageId;
    }

    @NonNull
    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(@NonNull String messageId) {
        mMessageId = messageId;
    }
}
