/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.adapters.ConnectMainViewPagerAdapter
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.BottomNavigationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Thaddeus Dannar on 9/20/23.
 */
@HiltViewModel
class BottomSheetBottomNavigationMoreViewModel @Inject constructor(
    application: Application,
    val sessionManager: SessionManager
) : BaseViewModel(application) {

    var moreNavigationItemsMutableLiveData: MutableLiveData<List<BottomNavigationItem>> = MutableLiveData()
    var progressIndicatorMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var isLoadingMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getUnreadLiveData(featureType: ConnectMainViewPagerAdapter.FeatureType): LiveData<Int>? {
        when (featureType) {
            ConnectMainViewPagerAdapter.FeatureType.Rooms -> {
                return sessionManager.getRoomsMessagesCountLiveData(
                    listOf(
                        RoomsEnums.ConnectRoomsTypes.MY_ROOM.value,
                        RoomsEnums.ConnectRoomsTypes.PRIVATE_ROOM.value,
                        RoomsEnums.ConnectRoomsTypes.PUBLIC_ROOM.value,
                        RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM.value
                    )
                )
            }
            ConnectMainViewPagerAdapter.FeatureType.Chat -> {
                return sessionManager.getRoomsMessagesCountLiveData(
                    listOf(
                        RoomsEnums.ConnectRoomsTypes.INDIVIDUAL_CONVERSATION.value,
                        RoomsEnums.ConnectRoomsTypes.MY_CONVERSATION.value,
                        RoomsEnums.ConnectRoomsTypes.GROUP_CONVERSATION.value
                    )
                )
            }
            else -> {
                return null
            }
        }
    }

}