package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 5/4/20.
 */
@Root(name = "conferenceSchedule", strict = false)
public class BroadsoftConferenceSchedule {

    @Nullable
    @Element(name = "scheduleReservationless", required = false)
    private BroadsoftScheduleReservationless mScheduleReservationless;

}
