package com.nextiva.nextivaapp.android.features.messaging.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommonSmsViewModel @Inject constructor(
    private val mDbManager: DbManager,
    private val dbManagerKt: DbManagerKt,
    val sipManager: PJSipManager
) : ViewModel() {

    private val _toolbarTitle = MutableLiveData<String?>()
    val toolbarTitle: LiveData<String?> = _toolbarTitle

    val activeCallLiveData = sipManager.activeCallLiveData

    fun updateToolbarTitle(toolbarTitle: String?) {
        _toolbarTitle.value = toolbarTitle
    }

    fun getContactFromPhoneNumber(phoneNumber: String?): NextivaContact {
        return mDbManager.getConnectContactFromPhoneNumberInThread(phoneNumber).value
            ?: NextivaContact(phoneNumber, Enums.Contacts.ContactTypes.CONNECT_UNKNOWN)
    }

    suspend fun getContactFromPhoneNumberInThread(phoneNumber: String): NextivaContact? {
        return dbManagerKt.getContactFromPhoneNumberInThread(phoneNumber).value
    }
}



