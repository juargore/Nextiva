package com.nextiva.nextivaapp.android.models.net.broadsoft.conference;

import androidx.annotation.Nullable;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by Thaddeus Dannar on 2020-02-19.
 */
@Root(name = "conferenceParticipantList", strict = false)
public class ConferenceParticipantList {

    @Nullable
    @ElementList(name = "conferenceParticipant", required = false)
    private ArrayList<ConferenceParticipant> mConferenceParticipant;

    public ConferenceParticipantList() {
    }

    @Nullable
    public ArrayList<ConferenceParticipant> getConferenceParticipant() {
        return mConferenceParticipant;
    }

}