package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 5/1/20.
 */

@Root(name = "MeetMeConferencingUserBridges", strict = false)
public class BroadsoftMeetMeConferencingUserBridges {

    @Nullable
    @Element(name = "userBridge", required = false)
    private BroadsoftUserBridge mUserBridge;

    public BroadsoftMeetMeConferencingUserBridges() {
    }

    @Nullable
    public BroadsoftUserBridge getUserBridge() {
        return mUserBridge;
    }

    public void setUserBridge(@Nullable final BroadsoftUserBridge userBridge) {
        mUserBridge = userBridge;
    }
}
