/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbGroup;
import com.nextiva.nextivaapp.android.models.NextivaContact;

/**
 * Created by adammacdonald on 2/8/18.
 */

public class ContactListItem extends SimpleBaseListItem<NextivaContact> {

    private final boolean mListItemLongClickable;
    @Nullable
    private final String mSearchTerm;
    @Enums.AvatarState.StateType
    private String mAvatarState;
    private DbGroup mGroup;

    public String getmConversationType() {
        return mConversationType;
    }

    public void setmConversationType(String mConversationType) {
        this.mConversationType = mConversationType;
    }

    private String mConversationType;

    public ContactListItem(@NonNull NextivaContact data,
                           @Nullable String searchTerm,
                           boolean listItemLongClickable) {

        super(data);
        mSearchTerm = searchTerm;
        mListItemLongClickable = listItemLongClickable;
    }

    public ContactListItem(@NonNull NextivaContact data,
                           @Nullable String searchTerm,
                           boolean listItemLongClickable,
                           DbGroup dbGroup) {

        super(data);
        mSearchTerm = searchTerm;
        mListItemLongClickable = listItemLongClickable;
        mGroup = dbGroup;

    }

    public ContactListItem(@NonNull NextivaContact data,
                           @Nullable String searchTerm,
                           boolean listItemLongClickable,
                           @Enums.AvatarState.StateType String avatarState) {

        super(data);
        mSearchTerm = searchTerm;
        mAvatarState = avatarState;
        mListItemLongClickable = listItemLongClickable;

    }


    public ContactListItem(@NonNull NextivaContact data,
                           @Nullable String searchTerm,
                           boolean listItemLongClickable,
                           @Enums.AvatarState.StateType String avatarState,
                           String conversationType) {

        super(data);
        mSearchTerm = searchTerm;
        mAvatarState = avatarState;
        mListItemLongClickable = listItemLongClickable;
        mConversationType = conversationType;
    }

    public boolean isListItemLongClickable() {
        return mListItemLongClickable;
    }

    @Nullable
    public String getSearchTerm() {
        return mSearchTerm != null ? mSearchTerm.toLowerCase() : "";
    }

    @Enums.AvatarState.StateType
    public String getAvatarState() {
        return mAvatarState != null ? mAvatarState : Enums.AvatarState.STATE_NOT_SELECTED;
    }

    public void setAvatarState(@Enums.AvatarState.StateType String state) {
        mAvatarState = state;
    }

    public DbGroup getGroup() {
        return mGroup;
    }

    public void setGroup(DbGroup group) {
        mGroup = group;
    }
}
