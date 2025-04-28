package com.nextiva.nextivaapp.android.models.net.broadsoft.ums;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class BroadsoftMarkMessagesReadBody implements Serializable {

    @Nullable
    @SerializedName("from")
    private String mFrom;

    @NonNull
    @SerializedName("readMessages")
    private ArrayList<BroadsoftMessageId> mMessageIds;

    public BroadsoftMarkMessagesReadBody(@Nullable String from,
                                         @NonNull ArrayList<BroadsoftMessageId> messageIds) {
        mFrom = from;
        mMessageIds = messageIds;
    }

    @Nullable
    public String getFrom() {
        return mFrom;
    }

    public void setFrom(@NonNull String from) {
        mFrom = from;
    }

    @NonNull
    public ArrayList<BroadsoftMessageId> getMessageIds() {
        return mMessageIds;
    }

    public void setMessageIds(@NonNull ArrayList<BroadsoftMessageId> messageIds) {
        mMessageIds = messageIds;
    }
}
