package com.nextiva.nextivaapp.android.models.net.broadsoft.conference;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 2020-02-19.
 */

@Root(name = "conferenceParticipant", strict = false)
public class ConferenceParticipant {

    @Nullable
    @Element(name = "callId", required = false)
    private String mCallId;

    public ConferenceParticipant() {
    }

    @Nullable
    public String getCallId() {
        return mCallId;
    }

    public void setCallId(@Nullable final String callId) {
        mCallId = callId;
    }
}
