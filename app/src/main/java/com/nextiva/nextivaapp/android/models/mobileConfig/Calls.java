package com.nextiva.nextivaapp.android.models.mobileConfig;

/**
 * Created by Thaddeus Dannar on 3/6/19.
 */
public class Calls {
    private boolean mAudioQos;
    private boolean mRejectWith486;
    private boolean mVideoQos;
    private Conference mConference;

    public Calls() {
    }

    public boolean isAudioQos() {
        return mAudioQos;
    }

    public void setAudioQos(final boolean audioQos) {
        this.mAudioQos = audioQos;
    }

    public boolean isRejectWith486() {
        return mRejectWith486;
    }

    public void setRejectWith486(final boolean rejectWith486) {
        this.mRejectWith486 = rejectWith486;
    }

    public boolean isVideoQos() {
        return mVideoQos;
    }

    public void setVideoQos(final boolean videoQos) {
        this.mVideoQos = videoQos;
    }
}
