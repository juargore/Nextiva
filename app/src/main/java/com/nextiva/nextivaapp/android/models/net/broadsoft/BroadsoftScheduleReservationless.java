package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 5/4/20.
 */
@Root(name = "conferenceSchedule", strict = false)
public class BroadsoftScheduleReservationless {


    @Nullable
    @Element(name = "startTime", required = false)
    private String mStartTime;
    @Nullable
    @Element(name = "endTime", required = false)
    private String mEndTime;

    public BroadsoftScheduleReservationless() {
    }

    @Nullable
    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(@Nullable final String startTime) {
        mStartTime = startTime;
    }

    @Nullable
    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(@Nullable final String endTime) {
        mEndTime = endTime;
    }
}
