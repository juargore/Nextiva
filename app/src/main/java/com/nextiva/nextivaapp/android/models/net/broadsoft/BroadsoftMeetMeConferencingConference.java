package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 5/1/20.
 */
@Root(name = "MeetMeConferencingConference", strict = false)
public class BroadsoftMeetMeConferencingConference {

    @Nullable
    @Element(name = "conferenceTitle", required = false)
    private String mConferenceTitle;
    @Nullable
    @Element(name = "restrictParticipants", required = false)
    private Boolean mRestrictParticipants;
    @Nullable
    @Element(name = "muteAllAttendeesOnEntry", required = false)
    private Boolean mMuteAllAttendeesOnEntry;
    @Nullable
    @Element(name = "endConferenceOnModeratorExit", required = false)
    private Boolean mEndConferenceOnModeratorExit;
    @Nullable
    @Element(name = "moderatorRequired", required = false)
    private Boolean mModeratorRequired;
    @Nullable
    @Element(name = "requireSecurityPin", required = false)
    private Boolean mRequireSecurityPin;
    @Nullable
    @Element(name = "allowUniqueIdentifier", required = false)
    private Boolean mAllowUniqueIdentifier;
    @Nullable
    @Element(name = "attendeeNotification", required = false)
    private String mAttendeeNotification;
    @Nullable
    @Element(name = "conferenceSchedule", required = false)
    private BroadsoftConferenceSchedule mConferenceSchedule;
    @Nullable
    @Element(name = "moderatorPin", required = false)
    private String mModeratorPin;

    public BroadsoftMeetMeConferencingConference() {
    }

    @Nullable
    public String getConferenceTitle() {
        return mConferenceTitle;
    }

    public void setConferenceTitle(@Nullable final String conferenceTitle) {
        mConferenceTitle = conferenceTitle;
    }

    @Nullable
    public Boolean getRestrictParticipants() {
        return mRestrictParticipants;
    }

    public void setRestrictParticipants(@Nullable final Boolean restrictParticipants) {
        mRestrictParticipants = restrictParticipants;
    }

    @Nullable
    public Boolean getMuteAllAttendeesOnEntry() {
        return mMuteAllAttendeesOnEntry;
    }

    public void setMuteAllAttendeesOnEntry(@Nullable final Boolean muteAllAttendeesOnEntry) {
        mMuteAllAttendeesOnEntry = muteAllAttendeesOnEntry;
    }

    @Nullable
    public Boolean getEndConferenceOnModeratorExit() {
        return mEndConferenceOnModeratorExit;
    }

    public void setEndConferenceOnModeratorExit(@Nullable final Boolean endConferenceOnModeratorExit) {
        mEndConferenceOnModeratorExit = endConferenceOnModeratorExit;
    }

    @Nullable
    public Boolean getModeratorRequired() {
        return mModeratorRequired;
    }

    public void setModeratorRequired(@Nullable final Boolean moderatorRequired) {
        mModeratorRequired = moderatorRequired;
    }

    @Nullable
    public Boolean getRequireSecurityPin() {
        return mRequireSecurityPin;
    }

    public void setRequireSecurityPin(@Nullable final Boolean requireSecurityPin) {
        mRequireSecurityPin = requireSecurityPin;
    }

    @Nullable
    public Boolean getAllowUniqueIdentifier() {
        return mAllowUniqueIdentifier;
    }

    public void setAllowUniqueIdentifier(@Nullable final Boolean allowUniqueIdentifier) {
        mAllowUniqueIdentifier = allowUniqueIdentifier;
    }

    @Nullable
    public String getAttendeeNotification() {
        return mAttendeeNotification;
    }

    public void setAttendeeNotification(@Nullable final String attendeeNotification) {
        mAttendeeNotification = attendeeNotification;
    }

    @Nullable
    public BroadsoftConferenceSchedule getConferenceSchedule() {
        return mConferenceSchedule;
    }

    public void setConferenceSchedule(@Nullable final BroadsoftConferenceSchedule conferenceSchedule) {
        mConferenceSchedule = conferenceSchedule;
    }

    @Nullable
    public String getModeratorPin() {
        return mModeratorPin;
    }

    public void setModeratorPin(@Nullable final String moderatorPin) {
        mModeratorPin = moderatorPin;
    }
}
