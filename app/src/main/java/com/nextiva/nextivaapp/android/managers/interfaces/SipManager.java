package com.nextiva.nextivaapp.android.managers.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.constants.Enums.AudioDevices.AudioDevice;
import com.nextiva.nextivaapp.android.models.AudioCodec;
import com.nextiva.nextivaapp.android.models.CallInfo;
import com.nextiva.nextivaapp.android.models.IncomingCall;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.models.PermissionRequest;
import com.nextiva.nextivaapp.android.models.PushNotificationCallInfo;
import com.nextiva.nextivaapp.android.models.SingleEvent;
import com.nextiva.nextivaapp.android.models.VideoCodec;
import com.nextiva.nextivaapp.android.sip.CallSession;
import com.nextiva.nextivaapp.android.sip.SipMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Thaddeus Dannar on 4/4/18.
 */
@SuppressWarnings("unused")
public interface SipManager {

    interface OnAudioDevicesChanged {
        void onAudioDevicesChanged(AudioDevice current, Set<AudioDevice> devices);
    }

    void initializeSip();

    void register();

    void register(PushNotificationCallInfo pushNotificationCallInfo);

    void register(CallInfo callInfo);

    Runnable initializeSipRunnable();

    void unRegister();

    void initializeSIPManager();

    void initializeSIPManager(PushNotificationCallInfo pushNotificationCallInfo);

    void setupSipPermissions(@NonNull AppCompatActivity activity);

    boolean endCall();

    boolean endCall(Long id);

    boolean endCall(@Nullable Long id, boolean shouldHangUpOnServer);

    boolean isCallActive();

    long getActiveCallSessionId();

    boolean isCallMute();

    boolean isCallHold();

    boolean isRemoteHold();

    boolean isSpeakerPhone();

    void hold(final boolean isHold);

    void hold(boolean isHold, @Nullable FrameLayout localVideoLayout, @Nullable FrameLayout remoteVideoLayout);

    void holdInBackground(long sessionId, final boolean isHold);

    void speakerPhone(boolean isSpeaker);

    int setAudioDevice(AudioDevice device);

    void onAudioDeviceChanged(Enums.AudioDevices.AudioDevice current, Set<AudioDevice> devices);
//
//    void addAudioChangedListener(PortSipManager.OnAudioDevicesChanged listener);
//
//    void removeAudioChangedListener(PortSipManager.OnAudioDevicesChanged listener);

    void startCall(@NonNull CallInfo callInfo, @Nullable String retrievalNumber, @NonNull FrameLayout localVideo, @NonNull FrameLayout remoteVideo);

    void startCall(@NonNull CallInfo callInfo, @NonNull FrameLayout localVideo, @NonNull FrameLayout remoteVideo);

    void startCall(@NonNull CallInfo callInfo);

    boolean isCallQueued();

    void cancelQueuedCall();

    void startCallFromPushNotificationCallInfo();

    void startQueuedCall();

    void addVideo(@NonNull FrameLayout localVideo, @NonNull FrameLayout remoteVideo);

    void addVideo(boolean fromRemote, @NonNull FrameLayout localVideo, @NonNull FrameLayout remoteVideo);

    void removeVideo();

    void removeVideo(long sessionId);

    void freezeLocalVideo();

    void resumeLocalVideo();

    void declineVideo();

    boolean isVideoEnabled();

    void playDialerKeyPress(@NonNull String keyPressed);

    void playDialerKeyPress(@NonNull String keyPressed, boolean shouldSendViaSip);

    int getCallCount();

    ArrayList<CallInfo> getActiveCallInfoList();

    ArrayList<CallInfo> getHoldCallInfoList();

    boolean swapCall(FrameLayout localVideo, FrameLayout remoteVideo);

    NextivaContact getNextivaContact();

    String getDisplayName();

    String getPhoneNumber();

    Intent createActiveCallScreenIntent(CallInfo callInfo);

    void setIsUIUpdateNeeded(boolean isUIUpdateNeeded);

    Boolean getIsUIUpdateNeeded();

    void showOngoingCallNotifications(@NonNull CallInfo callInfo, String state);

    void cancelOnCallNotification();

    void switchCamera(@Enums.Sip.CameraTypes.Type int cameraType);

    void setActivityForCallDetails(@NonNull AppCompatActivity mainActivity);

    void setLiveCallerName(@NonNull String callerName);

    void setLivePhoneNumber(@NonNull String phoneNumber);

    void setLiveCallState(@NonNull String state);

    void setLiveIsCallActive(boolean isCallActive);

    void setLiveIsHold(boolean isHold);

    void setLiveIsRemoteHold(final boolean isRemoteHold);

    void setLiveIsMute(boolean isMute);

    void setLiveIsVideo(final boolean liveIsVideo);

    void setLiveCameraState(final @Enums.Sip.CameraTypes.Type int cameraType);

    void setInviteFailureSessionId(@NonNull Long id);

    void clearCallLiveData();

    void setIncomingCallActivity(Activity incomingCallActivity);

    void answerIncomingCall(Activity activity, IncomingCall incomingCall);

    void rejectIncomingCall(IncomingCall incomingCall);

    void setDisplayName(String displayNameToShow);

    void setCurrentCodecsLiveData(ArrayList<AudioCodec> audioCodecs, ArrayList<VideoCodec> videoCodecs);

    LiveData<String> getCurrentCodecsLiveData();

    void setCurrentTrackingIdLiveData(String trackingId);

    LiveData<String> getCurrentTrackingIdLiveData();

    void setPhoneNumber(String phoneNumber);

    void setNextivaContact(NextivaContact nextivaContact);

    void showActiveCallScreen(Activity activity, @Nullable CallInfo callInfo);

    void setIncomingCallViewModel(final IncomingCall incomingCall);

    void clearIncomingCallData();

    void wakeUpDueToIncomingVoip();

    void setIncomingCallIntent(Intent intent);

    void stopIncomingCall();

    void setPushNotificationCallInfo(PushNotificationCallInfo pushNotificationCallInfo);

    void showPushNotificationIncomingCall(final PushNotificationCallInfo pushNotificationCallInfo);

    void showIncomingCall(final CallInfo callInfo, final String sipMessage);

    void muteRingtone();

    LiveData<String> getActiveCallStatusLiveData();

    LiveData<Boolean> getIsCallActiveLiveData();

    ArrayList<CallSession> getCallSessionList();

    LiveData<CallSession> getActiveCallSessionLiveData();

    LiveData<CallSession> getPassiveCallSessionLiveData();

    LiveData<String> getStateMutableLiveData();

    LiveData<Boolean> getIsCallActiveMutableLiveData();

    LiveData<Boolean> getIsSpeakerEnabledLiveData();

    LiveData<Boolean> getIsMuteEnabledLiveData();

    LiveData<Boolean> getIsRemoteHoldEnabledLiveData();

    LiveData<Boolean> getIsVideoEnabledLiveData();

    LiveData<SingleEvent<CallInfo>> getRequestToAddVideoLiveData();

    LiveData<Integer> getCameraStateLiveData();

    LiveData<IncomingCall> getIncomingCallLiveData();

    LiveData<String> getActiveCallDurationLiveData();

    LiveData<SingleEvent<Long>> getInviteFailureLiveData();

    LiveData<PermissionRequest> getPermissionsLiveData();

    LiveData<Boolean> getIsJitterIssueInCallLiveData();

    LiveData<Integer> getJitterSentLiveData();

    LiveData<Integer> getJitterReceivedLiveData();

    LiveData<Boolean> getIsLostPacketsIssueInCallLiveData();

    LiveData<Integer> getLostPacketsSentLiveData();

    LiveData<Integer> getLostPacketsReceivedLiveData();

    LiveData<CallSession> getSessionUpdatedLiveData();

    void setSessionUpdated(CallSession session);

    void setIsDisplayCallIssueWarningEnabled(boolean isEnabled);

    boolean getIsDisplayCallIssueWarningEnabled();

    IncomingCall getIncomingCall();

    void updateFromSipMessage(Long sessionId, SipMessage sipMessage);

    void playDTMFOnConnect();

//    String logVideo(PortSipStatistic sentStatistics, PortSipStatistic sentCodec, PortSipStatistic receivedStatistics, PortSipStatistic receivedCodec);
//
//    String logAudio(PortSipStatistic sentStatistics, PortSipStatistic sentCodec, PortSipStatistic receivedStatistics, PortSipStatistic receivedCodec);

    void clearCallTimer();

    void startCallTimer();

    void stopCallTimer();

    void stopRinger();

    boolean isRegistered();

    void updateSipVideoBitrate(@NonNull String videoCodec);

    void updateSipVideoBitrate(int bitrateInKbsp);

    void requestToAddVideo(long sessionId);

    void requestToAddVideo(long sessionId, boolean hasVideo);

    @Deprecated
    Long getRejectedCall();

    void setRemoteHold(boolean isRemoteHold);

    void refreshRegistration();

    String getInstanceID();

    void transferCall(String transferNumber);

    void warmTransferCall();

    void startConferenceCall(CallInfo numberToConference, FrameLayout localVideo, FrameLayout remoteVideo);

    boolean isPresentCallConference();

    boolean isPassiveCallConference();

    void updateCurrentLineInfo(CallInfo callInfo);

    void connectionHasUpdated();

    void callConferenceQueue();

    void mergeCall();

    void enableVideo();

    void logLinesStates();

    void setProximityDetection(@Enums.AppStates.AppState int appState);

    void setProximityDetection(boolean enableProximityDetection);

    void setEchoCancellation(boolean enableEchoCancellation);

    List<AudioCodec> getAudioCodecs();

    List<VideoCodec> getVideoCodecs();

    void setAudioCodecs(ArrayList<AudioCodec> audioCodecs);

    void setVideoCodecs(ArrayList<VideoCodec> videoCodecs);

    ArrayList<AudioCodec> getAudioCodecsFromString(String sipString);

    ArrayList<VideoCodec> getVideoCodecsFromString(String sipString);

    void setPhoneState(String phoneState);

    String getPhoneState();

    void postJitterIssue(boolean isJitterIssue);

    void postLostPacketsIssue(boolean isLostPacketsIssue);

    void postJitterSent(int jitter);

    void postJitterReceived(int jitter);

    void postLostPacketsSent(int lostPackets);

    void postLostPacketsReceived(int lostPackets);

    AudioDevice getCurrentAudioDevice();
}

