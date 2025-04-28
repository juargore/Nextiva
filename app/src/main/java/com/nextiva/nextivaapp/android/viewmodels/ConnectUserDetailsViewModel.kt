package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import android.telephony.PhoneNumberUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.ListHeaderRow
import com.nextiva.nextivaapp.android.models.NextivaContact
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ConnectUserDetailsViewModel @Inject constructor(
    application: Application,
    val sessionManager: SessionManager
) : BaseViewModel(application) {

    private var baseListItemsLiveData: MutableLiveData<ArrayList<BaseListItem>> = MutableLiveData()
    private var headerListItems: ArrayList<ConnectContactDetailHeaderListItem>? = ArrayList()

    fun getUserContact(): NextivaContact {
        val userContact = NextivaContact()
        userContact.firstName = sessionManager.userDetails?.firstName
        userContact.lastName = sessionManager.userDetails?.lastName
        return userContact
    }

    fun getUsersAvatarWithFullName(): AvatarInfo {
        return AvatarInfo.Builder()
            .setDisplayName(sessionManager.userDetails?.fullName)
            .setPresence(null)
            .setFontAwesomeIconResId(R.string.fa_user)
            .isConnect(true)
            .build()
    }

    fun getUserDetailListItems() {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        if (headerListItems.isNullOrEmpty()) {
            headerListItems = ArrayList()
            headerListItems?.add(
                ConnectContactDetailHeaderListItem(
                    ListHeaderRow(),
                    getUserContactProfileListItems(),
                    Enums.Platform.ConnectContactDetailSections.PRIMARY,
                    isExpanded = true,
                    isShowingMore = false,
                    shouldShowHeaderDetails = false
                )
            )

            // Commenting the time zone section temporarily for the 26.3.0 release

           /* headerListItems?.add(
                ConnectContactDetailHeaderListItem(
                    ListHeaderRow(),
                    getUserAdditionalProfileListItems(),
                    Enums.Platform.ConnectContactDetailSections.ADDITIONAL,
                    isExpanded = true,
                    isShowingMore = false,
                    shouldShowHeaderDetails = false
                )
            )*/
        }

        headerListItems?.forEach {
            when (it.itemType) {
                Enums.Platform.ConnectContactDetailSections.PRIMARY -> addHeaderListItem(
                    it,
                    listItems,
                    getUserContactProfileListItems()
                )
                Enums.Platform.ConnectContactDetailSections.ADDITIONAL -> addHeaderListItem(
                    it,
                    listItems,
                    getUserAdditionalProfileListItems()
                )
            }
        }

        baseListItemsLiveData.value = listItems
    }

    private fun addHeaderListItem(
        headerListItem: ConnectContactDetailHeaderListItem,
        baseListItemsList: java.util.ArrayList<BaseListItem>,
        childrenListItemsList: java.util.ArrayList<BaseListItem>
    ) {
        if (childrenListItemsList.isNotEmpty()) {
            headerListItem.baseListItemsList?.clear()

            headerListItem.baseListItemsList?.addAll(childrenListItemsList)
            baseListItemsList.add(headerListItem)

            if (headerListItem.isExpanded) {
                baseListItemsList.addAll(childrenListItemsList)
            }
        }
    }

    private fun getUserAdditionalProfileListItems(): ArrayList<BaseListItem> {
        val listItems: ArrayList<BaseListItem> = ArrayList()
        listItems.add(
            ConnectContactDetailListItem(
                application.getString(R.string.user_details_item_time_zone),
                application.getString(R.string.user_details_item_time_zone),
                application.getString(R.string.user_details_no_data_available),
                isClickable = false,
                iconId = R.string.fa_clock,
                iconType = Enums.FontAwesomeIconType.REGULAR,
                actionType = Enums.Platform.ConnectContactDetailClickAction.NONE
            )
        )
        return listItems
    }

    private fun getUserContactProfileListItems(): ArrayList<BaseListItem> {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        sessionManager.userDetails?.telephoneNumber?.let { telephoneNumber ->
            val number = PhoneNumberUtils.formatNumber(telephoneNumber, Locale.getDefault().country)
            if (!number.isNullOrEmpty()) {
                listItems.add(
                    ConnectContactDetailListItem(
                        application.getString(R.string.user_details_item_work),
                        application.getString(R.string.user_details_item_work),
                        number,
                        isClickable = false,
                        iconId = R.string.fa_phone_alt,
                        iconType = Enums.FontAwesomeIconType.REGULAR,
                        actionType = Enums.Platform.ConnectContactDetailClickAction.NONE
                    )
                )
            }
        }

        sessionManager.userDetails?.extension?.let { extension ->
            if (extension.isNotEmpty()) {
                listItems.add(
                    ConnectContactDetailListItem(
                        application.getString(R.string.user_details_item_work_extension),
                        application.getString(R.string.user_details_item_work_extension),
                        extension,
                        isClickable = false,
                        iconId = R.string.fa_phone_alt,
                        iconType = Enums.FontAwesomeIconType.REGULAR,
                        actionType = Enums.Platform.ConnectContactDetailClickAction.NONE
                    )
                )
            }
        }

        sessionManager.userDetails?.email?.let { email ->
            if (email.isNotEmpty()) {
                listItems.add(
                    ConnectContactDetailListItem(
                        application.getString(R.string.user_details_item_primary_email),
                        application.getString(R.string.user_details_item_primary_email),
                        email,
                        isClickable = false,
                        iconId = R.string.fa_envelope,
                        iconType = Enums.FontAwesomeIconType.REGULAR,
                        actionType = Enums.Platform.ConnectContactDetailClickAction.NONE
                    )
                )
            }
        }

        return listItems
    }

    fun getBaseListItemsLiveData(): LiveData<ArrayList<BaseListItem>> {
        return baseListItemsLiveData
    }

}