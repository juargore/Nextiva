package com.nextiva.nextivaapp.android.models;

import com.nextiva.nextivaapp.android.constants.Enums;

public class AvatarStateChange {
    private NextivaContact mNextivaContact;
    private @Enums.AvatarState.StateType String mAvatarState;

    public AvatarStateChange(String mAvatarState, NextivaContact mNextivaContact) {
        this.mAvatarState = mAvatarState;
        this.mNextivaContact = mNextivaContact;
    }

    public NextivaContact getmNextivaContact() {
        return mNextivaContact;
    }

    public void setmNextivaContact(NextivaContact mNextivaContact) {
        this.mNextivaContact = mNextivaContact;
    }

    public @Enums.AvatarState.StateType String getmAvatarState() {
        return mAvatarState;
    }

    public void setmAvatarState(@Enums.AvatarState.StateType String mAvatarState) {
        this.mAvatarState = mAvatarState;
    }
}
