/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.rooms;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsHeaderListItem;
import com.nextiva.nextivaapp.android.features.rooms.view.ConnectRoomsListItem;

public interface RoomsMasterListListener extends MasterListListener {

    void onConnectRoomsHeaderListItemClicked(@NonNull ConnectRoomsHeaderListItem listItem);

    void onConnectRoomListItemLongClicked(@NonNull ConnectRoomsListItem listItem);

    void onConnectRoomListItemClicked(@NonNull ConnectRoomsListItem listItem);

    void onConnectRoomFavoriteIconClicked(@NonNull ConnectRoomsListItem listItem);

}
