package com.nextiva.nextivaapp.android.models;

import androidx.annotation.NonNull;

public class AvatarEvent {

    @NonNull
    private final byte[] mAvatarBytes;
    @NonNull
    private final String mJid;

    public AvatarEvent(@NonNull byte[] avatarBytes, @NonNull String jid) {
        mAvatarBytes = avatarBytes;
        mJid = jid;
    }

    @NonNull
    public byte[] getAvatarBytes() {
        return mAvatarBytes;
    }

    @NonNull
    public String getJid() {
        return mJid;
    }
}
