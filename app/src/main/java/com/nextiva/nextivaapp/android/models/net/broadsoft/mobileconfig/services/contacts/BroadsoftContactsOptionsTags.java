/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.contacts;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftContactsOptionsTags implements Serializable {

    @Nullable
    @Element(name = "chatgchat", required = false)
    private String mChatGChat;
    @Nullable
    @Element(name = "filetransfer", required = false)
    private String mFileTransfer;
    @Nullable
    @Element(name = "imageshare", required = false)
    private String mImageShare;
    @Nullable
    @Element(name = "videoshare", required = false)
    private String mVideoShare;
    @Nullable
    @Element(name = "socialpresence", required = false)
    private String mSocialPresence;
    // operator-tags

    public BroadsoftContactsOptionsTags() {
    }

    @Nullable
    public String getChatGChat() {
        return mChatGChat;
    }

    @Nullable
    public String getFileTransfer() {
        return mFileTransfer;
    }

    @Nullable
    public String getImageShare() {
        return mImageShare;
    }

    @Nullable
    public String getVideoShare() {
        return mVideoShare;
    }

    @Nullable
    public String getSocialPresence() {
        return mSocialPresence;
    }
}
