/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.rooms;

class RoomsEnums {

    enum class ConnectRoomsGroups {
        FAVORITES,
        ROOMS
    }

    enum class ConnectRoomsTypes(val value: String) {
        INDIVIDUAL_CONVERSATION("INDIVIDUAL_CONVERSATION"),
        MY_CONVERSATION("MY_CONVERSATION"),
        GROUP_CONVERSATION("GROUP_CONVERSATION"),
        PRIVATE_ROOM("PRIVATE_ROOM"),
        PUBLIC_ROOM("PUBLIC_ROOM"),
        MY_ROOM("MY_ROOM"),
        CURRENT_USER_MY_ROOM("CURRENT_USER_MY_ROOM"),
        IN_VIDEO_CHAT("IN_VIDEO_CHAT"),
        UNKNOWN("UNKNOWN")
    }

}
