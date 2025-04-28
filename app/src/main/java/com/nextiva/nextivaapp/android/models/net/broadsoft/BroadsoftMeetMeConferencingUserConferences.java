package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by Thaddeus Dannar on 5/1/20.
 */

@Root(name = "MeetMeConferencingUserConferences", strict = false)
public class BroadsoftMeetMeConferencingUserConferences {

    @Nullable
    @ElementList(name = "userConference", inline = true, required = false)
    private ArrayList<BroadsoftUserConference> mUserConference;

    public BroadsoftMeetMeConferencingUserConferences() {
    }

    @Nullable
    public ArrayList<BroadsoftUserConference> getUserConference() {
        return mUserConference;
    }

    public void setUserConference(@Nullable final ArrayList<BroadsoftUserConference> userConference) {
        mUserConference = userConference;
    }
}
