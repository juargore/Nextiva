package com.nextiva.nextivaapp.android.sip;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.CallInfo;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.pjsip.pjsip_lib.sipservice.CallState;

import java.util.ArrayList;

public class CallSession {

    private static final int INVALID_SESSION_ID = -1;

    private String mRemote;
    private String mDisplayName;
    private boolean mHasVideo;
    private long mSessionID;
    private boolean mIsHold;
    private long mCallStateLong;
    private boolean mIsCallSDP;
    private boolean mIsCallActive;
    private boolean mIsCallConference;
    private boolean mIsJoinToConference;
    private String mCallType;
    private String mLineName;
    private CallState mCallState;
    @NonNull
    private ArrayList<CallInfo> mCallInfoArrayList = new ArrayList<>();
    @Nullable
    private String mDialedSipAddress;
    private long mCallStartTime;
    private String mCallId;
    private String mTrackingId;
    private String mConferenceId;


    public boolean isIdle() {
        return mCallState == CallState.FAILED || mCallState == CallState.CLOSED;
    }

    public CallSession() {
        mRemote = null;
        mDisplayName = null;
        mHasVideo = false;
        mSessionID = INVALID_SESSION_ID;
        mCallState = CallState.CLOSED;
        mIsJoinToConference = false;
        mIsCallConference = false;
        mTrackingId = null;
        mConferenceId = null;
    }

    public void reset() {
        mRemote = null;
        mDisplayName = null;
        mHasVideo = false;
        mIsHold = false;
        mIsCallActive = false;
        mSessionID = INVALID_SESSION_ID;
        mCallInfoArrayList.clear();
        mCallState = CallState.CLOSED;
        mIsJoinToConference = false;
        mIsCallConference = false;
        mCallStartTime = 0L;
        mCallId = null;
        mTrackingId = null;
        mConferenceId = null;
        clearCallTimer();
    }

    /**
     * This will be set creation on the call and never changed.
     *
     * @return the original phone number dialed.
     */
    @Nullable
    public String getDialedSipAddress() {
        return mDialedSipAddress;
    }

    /**
     * Callers on the present call as best known.
     *
     * @return A list of all callers on the present call with #CallInfo.
     */
    @NonNull
    public ArrayList<CallInfo> getCallInfoArrayList() {
        return mCallInfoArrayList;
    }

    /**
     * @param callInfoArrayList A list on participants on the call.
     */
    public void setCallInfoArrayList(@NonNull final ArrayList<CallInfo> callInfoArrayList) {
        mCallInfoArrayList = callInfoArrayList;
    }

    /**
     * @param callInfo to add to an existing call
     */
    public void addCallInfo(@NonNull final CallInfo callInfo) {
        mCallInfoArrayList.add(callInfo);
    }

    /**
     * @param caller Participants call info to remove from call info list
     */
    public void removeCallerInfo(final int caller) {
        mCallInfoArrayList.remove(caller);
    }

    public void clearCallerInfo() {
        mCallInfoArrayList.clear();
    }

    public static int getInvalidSessionId() {
        return INVALID_SESSION_ID;
    }

    public String getRemote() {
        return mRemote;
    }

    public void setRemote(final String remote) {
        mRemote = remote;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(final String displayName) {
        mDisplayName = displayName;
    }

    public boolean isHasVideo() {
        return mHasVideo;
    }

    public void setHasVideo(final boolean hasVideo) {
        mHasVideo = hasVideo;
    }

    public long getSessionID() {
        return mSessionID;
    }

    public void setSessionID(final long sessionID) {
        mSessionID = sessionID;
    }

    public boolean isHold() {
        return mIsHold;
    }

    public void setHold(final boolean isHold) {
        mIsHold = isHold;
    }


    public String getLineName() {
        return mLineName;
    }

    public void setLineName(final String lineName) {
        mLineName = lineName;
    }

    public CallState getCallState() {
        return mCallState;
    }

    public void setCallState(final CallState callState) {
        mCallState = callState;
    }

    public void setDialedSipAddress(@NonNull final String dialedSipAddress) {
        mDialedSipAddress = dialedSipAddress;
    }


    public void setCallTimer(Long callTimer) {
        mCallStateLong = callTimer;
    }

    public Long getCallTimer() {
        return mCallStateLong;
    }

    public void clearCallTimer() {
        mCallStateLong = 0L;
    }

    public void startCallTimer() {
        mCallStateLong = mCallStartTime;
        LogUtil.d("Sip Timer startCallTimer: " + mCallStateLong);
    }

    public long getCallTimerInMillis() {
        if (mCallStateLong == 0L) {
            LogUtil.d("Sip Timer getCallTimerInMillis: " + mCallStateLong);
            return mCallStateLong;
        }

        return System.currentTimeMillis() - mCallStateLong;
    }

    public long getCallTimerInSeconds() {
        return getCallTimerInMillis() / 1000;
    }

    boolean isCallSDP() {
        return mIsCallSDP;
    }

    public void setCallSDP(final boolean callSDP) {
        mIsCallSDP = callSDP;
    }


    public boolean isCallActive() {
        return mIsCallActive;
    }

    public void setIsCallActive(final boolean isCallActive) {
        mIsCallActive = isCallActive;
    }

    public boolean isCallConference() {
        return mIsCallConference;
    }

    public void setCallConference(final boolean callConference) {
        mIsCallConference = callConference;
    }

    public boolean isJoinToConference() {
        return mIsJoinToConference;
    }

    public void setJoinToConference(final boolean joinToConference) {
        mIsJoinToConference = joinToConference;
    }
    public String getCallType() {
        return mCallType;
    }

    public void setCallType(String mCallType) {
        this.mCallType = mCallType;
    }

    public Long getCallStartTime() {
        return mCallStartTime;
    }

    public void setCallStartTime(Long callStartTime) {
        mCallStartTime = callStartTime;
    }

    public String getCallId() {
        return mCallId;
    }

    public void setCallId(String callId) {
        mCallId = callId;
    }

    public String getTrackingId() {
        return mTrackingId;
    }

    public void setTrackingId(String trackingId) {
        mTrackingId = trackingId;
    }

    public String getConferenceId() {
        return mConferenceId;
    }

    public void setConferenceId(String conferenceId) {
        mConferenceId = conferenceId;
    }
}


