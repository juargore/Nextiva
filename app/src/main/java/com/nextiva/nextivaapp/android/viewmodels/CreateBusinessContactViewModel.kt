package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateBusinessContactViewModel @Inject constructor(
    application: Application,
    val dbManager: DbManager,
    val dbManagerKt: DbManagerKt,
    val sipManager: PJSipManager,
    val platformContactsRepository: PlatformContactsRepository
) : BaseViewModel(application) {

    private val _dataValidation = MutableLiveData<Event<Boolean>>()
    val dataValidation: LiveData<Event<Boolean>> = _dataValidation
    fun triggerValidation() {
        _dataValidation.postValue(Event((true)))
    }

    val activeCallLiveData = sipManager.activeCallLiveData

    var nextivaContact: NextivaContact? = null
        set(value) {
            field = value
            mCompositeDisposable.add(
                    dbManager.getContactFromContactTypeId(value?.userId).subscribe { contact ->
                        field = contact
                    })
        }

    fun saveContact(contact: NextivaContact, finishCallback: () -> Unit) {
        mCompositeDisposable.add(
                platformContactsRepository.createContact(contact).subscribe { response ->
                    if (response?.isSuccessful == true && response.body() != null) {
                        mCompositeDisposable.add(
                                dbManager.saveContacts(arrayListOf(response.body()?.toNextivaContact()), UUID.randomUUID().toString(), false)
                                        .subscribe {
                                            finishCallback()
                                        })

                    } else {
                        finishCallback()
                    }
                })
    }

    fun patchContact(contact: NextivaContact, finishCallback: () -> Unit) {
        mCompositeDisposable.add(
                platformContactsRepository.patchContact(contact).subscribe { response ->
                    if (response?.isSuccessful == true && response.body() != null) {
                        val responseContact = response.body()?.toNextivaContact()
                        responseContact?.dbId = contact.dbId

                        mCompositeDisposable.add(
                                dbManager.updateContact(responseContact).subscribe {
                                    finishCallback()
                                })

                    } else {
                        finishCallback()
                    }
                })
    }

    suspend fun getContactFromPhoneNumber(phoneNumber: String): NextivaContact? {
        return dbManagerKt.getContactFromPhoneNumberInThread(phoneNumber).value
    }
}