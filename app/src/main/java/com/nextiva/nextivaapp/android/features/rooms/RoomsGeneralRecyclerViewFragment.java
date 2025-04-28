/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.rooms;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsHeaderListItem;
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsListItem;
import com.nextiva.nextivaapp.android.fragments.GeneralRecyclerViewFragment;

public abstract class RoomsGeneralRecyclerViewFragment extends GeneralRecyclerViewFragment implements RoomsMasterListListener {

    @Override
    public void onConnectRoomsHeaderListItemClicked(@NonNull ConnectRoomsHeaderListItem listItem) {
        if (mMasterListListener != null) {
            ((RoomsGeneralRecyclerViewFragment) mMasterListListener).onConnectRoomsHeaderListItemClicked(listItem);
        }
    }

    @Override
    public void onConnectRoomListItemClicked(@NonNull ConnectRoomsListItem listItem) {
        if (mMasterListListener != null) {
            ((RoomsGeneralRecyclerViewFragment)mMasterListListener).onConnectRoomListItemClicked(listItem);
        }
    }

    @Override
    public void onConnectRoomListItemLongClicked(@NonNull ConnectRoomsListItem listItem) {
        if (mMasterListListener != null) {
            ((RoomsGeneralRecyclerViewFragment)mMasterListListener).onConnectRoomListItemLongClicked(listItem);
        }
    }

    @Override
    public void onConnectRoomFavoriteIconClicked(@NonNull ConnectRoomsListItem listItem) {
        if (mMasterListListener != null) {
            ((RoomsGeneralRecyclerViewFragment) mMasterListListener).onConnectRoomFavoriteIconClicked(listItem);
        }
    }
}
