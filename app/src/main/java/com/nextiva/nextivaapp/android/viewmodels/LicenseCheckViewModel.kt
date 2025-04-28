package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ProductsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.PushNotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.xmpp.managers.NextivaXMPPConnectionActionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LicenseCheckViewModel @Inject constructor(nextivaApplication: Application, val connectionStateManager: ConnectionStateManager,
                                                val userRepository: UserRepository, val umsRepository: UmsRepository,
                                                val pushNotificationManager: PushNotificationManager, val xmppConnectionActionManager: NextivaXMPPConnectionActionManager,
                                                val schedulerProvider: SchedulerProvider, val dbManager: DbManager, val smsManagementRepository: SmsManagementRepository,
                                                val sessionManager: SessionManager, private val productsRepository: ProductsRepository) : BaseViewModel(nextivaApplication) {

    private var smsLicenseResultLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var voicemailTranscriptionLicenseResultLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var connectLicenseResultLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var videoGuestParticipantsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var videoLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var phoneNumberLiveData: MutableLiveData<String> = MutableLiveData()
    private var smsEnabledLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var teamSmsEnabledLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun runCheck() {
        checkLicenses()
    }

    private fun checkLicenses() {
        smsLicenseResultLiveData.value = sessionManager.products.isFeatureEnabled(Enums.License.Features.SMS_BASE)
        voicemailTranscriptionLicenseResultLiveData.value = sessionManager.products.isFeatureEnabled(Enums.License.Features.VOICEMAIL_TRANSCRIPTION)
        connectLicenseResultLiveData.value = sessionManager.products.isFeatureEnabled(Enums.License.Features.BASE_NEXTIVA_CONNECT)
        videoGuestParticipantsLiveData.value = sessionManager.products.isFeatureEnabled(Enums.License.Features.VIDEO_GUEST_PARTICIPANTS)
        videoLiveData.value = sessionManager.products.isFeatureEnabled(Enums.License.Features.VIDEO)
        phoneNumberLiveData.value = sessionManager.userDetails?.telephoneNumber
        smsEnabledLiveData.value = sessionManager.isSmsLicenseEnabled
        teamSmsEnabledLiveData.value = sessionManager.isTeamSmsLicenseEnabled
    }

    fun getTeamSmsLicenseResultLiveData(): LiveData<Boolean> {
        return teamSmsEnabledLiveData
    }

    fun getSmsLicenseResultLiveData(): LiveData<Boolean> {
        return smsLicenseResultLiveData
    }

    fun getVoicemailTranscriptionLicenseResultLiveData(): LiveData<Boolean> {
        return voicemailTranscriptionLicenseResultLiveData
    }

    fun getConnectLicenseResultLiveData(): LiveData<Boolean> {
        return connectLicenseResultLiveData
    }

    fun getVideoGuestLicenseResultLiveData(): LiveData<Boolean> {
        return videoGuestParticipantsLiveData
    }

    fun getVideoLicenseResultLiveData(): LiveData<Boolean> {
        return videoLiveData
    }

    fun getPhoneNumberResultLiveData(): LiveData<String> {
        return phoneNumberLiveData
    }

    fun getSmsEnabledResultLiveData(): LiveData<Boolean> {
        return smsEnabledLiveData
    }
}