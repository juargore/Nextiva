package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HealthCheckListItem
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomsReturn
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.MediaCallRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformNotificationOrchestrationServiceRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ProductsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.PushNotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.net.platform.DeviceBody
import com.nextiva.nextivaapp.android.models.net.platform.LogSubmit
import com.nextiva.nextivaapp.android.net.buses.RxBus
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.xmpp.managers.NextivaXMPPConnectionActionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HealthCheckViewModel @Inject constructor(nextivaApplication: Application, val connectionStateManager: ConnectionStateManager,
                                               val userRepository: UserRepository, val umsRepository: UmsRepository,
                                               private val platformNotificationOrchestrationServiceRepository: PlatformNotificationOrchestrationServiceRepository,
                                               val pushNotificationManager: PushNotificationManager, val xmppConnectionActionManager: NextivaXMPPConnectionActionManager,
                                               val schedulerProvider: SchedulerProvider, val dbManager: DbManager, val smsManagementRepository: SmsManagementRepository,
                                               val sipManager: PJSipManager, val sessionManager: SessionManager, private val productsRepository: ProductsRepository,
                                               private val platformRepository: PlatformRepository, val presenceRepository: PresenceRepository,
                                               private val roomsRepository: PlatformRoomsRepository, private val contactsRepository: PlatformContactsRepository,
                                               private val mediaCallRepository: MediaCallRepository) : BaseViewModel(nextivaApplication) {

    private val checkTimeOutSeconds = 30L

    private var voicePushListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_voice_push_registration))
    private var chatPushListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_chat_push_registration))
    private var nextivaOnePushListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_platform_push_registration))
    private var sipRegistrationListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_sip_registration))
    private var sipDeregistrationListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_sip_deregistration))
    private var xmppConnectionListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_xmpp_connection))
    private var smsApiListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_sms_api_health))
    private var vmTranscriptionApiListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_vm_api_health))
    private var smsV2ListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_sms_version_two))
    private var contactsListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_contacts))
    private var presenceListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_presence))
    private var phoneNumberOrchestrationListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_phone_number_orchestration))
    private var loggingServiceListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_logging_service))
    private var meetingsListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_meetings))
    private var chatRoomsListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_chat_rooms))
    private var attachmentsListItem = HealthCheckListItem(nextivaApplication.getString(R.string.health_check_attachments))

    var listItemsLiveData: MutableLiveData<ArrayList<BaseListItem>> = MutableLiveData()
    var checkFinished: MutableLiveData<Void?> = MutableLiveData()

    private var timeoutTimer: CountDownTimer? = null

    private var sipRegistrationStartTime: Long? = null
    private var sipDeregistrationStartTime: Long? = null
    private var xmppTextConnectionStartTime: Long? = null

    private var sipRegistrationFinished = false
    private var sipDeregistrationFinished = false
    private var xmppConnectionFinished = false

    private var isSmsEnabled = false
    private var isVmTranscriptionEnabled = false
    private var isNextivaOneEnabled = false
    private var listItems: ArrayList<BaseListItem> = ArrayList()

    init {
        initRxEventListeners()
        isSmsEnabled = sessionManager.isSmsEnabled
        isVmTranscriptionEnabled = sessionManager.isVoicemailTranscriptionEnabled
        isNextivaOneEnabled = sessionManager.isNextivaConnectEnabled

        if (isNextivaOneEnabled) {
            chatPushListItem.enabled = false
            xmppConnectionListItem.enabled = false

        } else {
            contactsListItem.enabled = false
            presenceListItem.enabled = false
            meetingsListItem.enabled = false
            chatRoomsListItem.enabled = false
        }

        if (!isSmsEnabled) {
            smsApiListItem.enabled = false
            smsV2ListItem.enabled = false
        }

        if (!isVmTranscriptionEnabled) {
            vmTranscriptionApiListItem.enabled = false
        }

        listItems.addAll(arrayListOf(voicePushListItem,
            chatPushListItem,
            nextivaOnePushListItem,
            sipRegistrationListItem,
            sipDeregistrationListItem,
            contactsListItem,
            presenceListItem,
            meetingsListItem,
            chatRoomsListItem,
            xmppConnectionListItem,
            smsApiListItem,
            smsV2ListItem,
            attachmentsListItem,
            vmTranscriptionApiListItem,
            phoneNumberOrchestrationListItem,
            loggingServiceListItem))

        listItemsLiveData.value = listItems
    }

    fun runCheck() {
        clearResults()
        setChecking()

        timeoutTimer = object: CountDownTimer(TimeUnit.SECONDS.toMillis(checkTimeOutSeconds), TimeUnit.SECONDS.toMillis(checkTimeOutSeconds)) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                if (!sipRegistrationFinished) {
                    sipRegistrationStartTime?.let { startTime ->
                        sipRegistrationListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, false)) }
                    }
                }
                if (!sipDeregistrationFinished) {
                    sipDeregistrationStartTime?.let { startTime ->
                        sipDeregistrationListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, false)) }
                    }
                }
                if (!xmppConnectionFinished) {
                    xmppTextConnectionStartTime?.let { startTime ->
                        xmppConnectionListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, false)) }
                    }
                }

                checkIfFinished()
            }
        }
        timeoutTimer?.start()

        sipRegistrationStartTime = null
        sipDeregistrationStartTime = null

        checkVoicePushRegistration()
        checkPlatformPushRegistration()
        checkSipRegistration()
        checkPhoneNumberOrchestrationHealth()
        checkLoggingServiceHealth()
        checkAttachmentsHealth()

        if (isNextivaOneEnabled) {
            checkContactsHealth()
            checkPresenceHealth()
            checkMeetingsHealth()
            checkChatRoomsHealth()

        } else {
            xmppTextConnectionStartTime = null

            checkUmsRegistration()
            checkXmppConnection()
        }

        if (isSmsEnabled) {
            checkSmsApiHealth()
            checkSmsV2Health()
        }

        if (isVmTranscriptionEnabled) {
            checkVmTranscriptionApiHealth()
        }
    }

    private fun setChecking() {
        listItems.forEach {
            (it as? HealthCheckListItem)?.let { listItem ->
                if (listItem.enabled) { listItem.isChecking = true }
            }
        }
    }

    private fun checkIfFinished() {
        if (listItems.firstOrNull { (it as? HealthCheckListItem)?.isChecking == true } == null) {
            timeoutTimer?.cancel()
            checkFinished.value = null
        }
    }

    private fun checkVoicePushRegistration() {
        val startTime = System.currentTimeMillis()
        mCompositeDisposable.add(userRepository.doesPushNotificationExist()
                .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(false))
                .observeOn(schedulerProvider.ui())
                .doOnError(this::recordException)
                .subscribe { success ->
                    voicePushListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, success)) }
                    checkIfFinished()
                })
    }

    private fun checkUmsRegistration() {
        val startTime = System.currentTimeMillis()
        mCompositeDisposable.add(umsRepository.registerDevice()
                .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(RxEvents.RegisterDeviceResponseEvent(false)))
                .observeOn(schedulerProvider.ui())
                .subscribe { responseEvent ->
                    chatPushListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, responseEvent.isSuccessful)) }
                    checkIfFinished()
                })
    }

    private fun checkPlatformPushRegistration() {
        val startTime = System.currentTimeMillis()
        mCompositeDisposable.add(platformNotificationOrchestrationServiceRepository.getDevice(null)
                .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(DeviceBody()))
                .observeOn(schedulerProvider.ui())
                .subscribe { deviceBody ->
                    nextivaOnePushListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, deviceBody?.firebaseRegistrationToken == pushNotificationManager.getToken())) }
                    checkIfFinished()
                })
    }

    private fun checkSipRegistration() {
        sipRegistrationStartTime = System.currentTimeMillis()
        sipManager.registerAccount()
    }

    private fun checkXmppConnection() {
        xmppTextConnectionStartTime = System.currentTimeMillis()

        if (connectionStateManager.isXmppConnected) {
            xmppConnectionActionManager.testConnection()

        } else {
            xmppConnectionListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - (xmppTextConnectionStartTime ?: System.currentTimeMillis()), false)) }
            checkIfFinished()
        }
    }

    private fun checkSmsApiHealth() {
        dbManager.successfullySentMessageId?.let { messageId ->
            val startTime = System.currentTimeMillis()
            smsManagementRepository.checkApiHealthByMessageId(messageId)
                    .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(false))
                    .observeOn(schedulerProvider.ui())
                    .subscribe { success ->
                        smsApiListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, success)) }
                        checkIfFinished()
                    }

        }
    }

    private fun checkVmTranscriptionApiHealth() {
        val startTime = System.currentTimeMillis()
        mCompositeDisposable.add(platformRepository.getVoicemails()
                .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(false))
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    vmTranscriptionApiListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, success)) }
                    checkIfFinished()
                })
    }

    private fun checkSmsV2Health() {
        dbManager.successfullySentMessageId?.let { messageId ->
            val startTime = System.currentTimeMillis()
            smsManagementRepository.checkV2ApiHealthByMessageId(messageId)
                .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(false))
                .observeOn(schedulerProvider.ui())
                .subscribe { success ->
                    smsV2ListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, success)) }
                    checkIfFinished()
                }
        }
    }

    private fun checkContactsHealth() {
        val startTime = System.currentTimeMillis()

        contactsRepository.checkApiHealth()
            .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(false))
            .observeOn(schedulerProvider.ui())
            .subscribe(object : DisposableSingleObserver<Boolean>() {
                override fun onSuccess(success: Boolean) {
                    contactsListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, success)) }
                    checkIfFinished()
                }

                override fun onError(e: Throwable) {
                    contactsListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, false)) }
                    checkIfFinished()
                }
            })
    }

    private fun checkPresenceHealth() {
        val startTime = System.currentTimeMillis()

        presenceRepository.checkApiHealth()
            .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(false))
            .observeOn(schedulerProvider.ui())
            .subscribe(object : DisposableSingleObserver<Boolean>() {
                override fun onSuccess(isSuccessful: Boolean) {
                    presenceListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, isSuccessful)) }
                    checkIfFinished()
                }

                override fun onError(e: Throwable) {
                    presenceListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, false)) }
                    checkIfFinished()
                }
            })
    }

    private fun checkPhoneNumberOrchestrationHealth() {
        val startTime = System.currentTimeMillis()

        productsRepository.getPhoneNumberInformation()
            .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(RxEvents.PhoneInformationResponseEvent(false)))
            .observeOn(schedulerProvider.ui())
            .subscribe(object : DisposableSingleObserver<RxEvents.PhoneInformationResponseEvent>() {
                override fun onError(e: Throwable) {
                    phoneNumberOrchestrationListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, false)) }
                    checkIfFinished()
                }

                override fun onSuccess(event: RxEvents.PhoneInformationResponseEvent) {
                    phoneNumberOrchestrationListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, event.isSuccessful)) }
                    checkIfFinished()
                }
            })
    }

    private fun checkLoggingServiceHealth() {
        val startTime = System.currentTimeMillis()

        platformRepository.postLogs(LogSubmit(null, ArrayList()))
            .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(RxEvents.LoggingResponseEvent(false)))
            .observeOn(schedulerProvider.ui())
            .subscribe(object : DisposableSingleObserver<RxEvents.LoggingResponseEvent>() {
                override fun onSuccess(event: RxEvents.LoggingResponseEvent) {
                    loggingServiceListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, event.isSuccessful)) }
                    checkIfFinished()
                }

                override fun onError(e: Throwable) {
                    loggingServiceListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, false)) }
                    checkIfFinished()
                }
            })
    }

    private fun checkMeetingsHealth() {
        val startTime = System.currentTimeMillis()

        mediaCallRepository.checkApiHealth()
            .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(false))
            .observeOn(schedulerProvider.ui())
            .subscribe(object : DisposableSingleObserver<Boolean>() {
                override fun onSuccess(success: Boolean) {
                    meetingsListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, success)) }
                    checkIfFinished()
                }

                override fun onError(e: Throwable) {
                    meetingsListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, false)) }
                    checkIfFinished()
                }
            })
    }

    private fun checkChatRoomsHealth() {
        val startTime = System.currentTimeMillis()

        roomsRepository.getMyRoom(true)
            .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(ConnectRoomsReturn(null, null)))
            .observeOn(schedulerProvider.ui())
            .subscribe(object : DisposableSingleObserver<ConnectRoomsReturn>() {
                override fun onSuccess(room: ConnectRoomsReturn) {
                    chatRoomsListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, room.totalCount != null)) }
                    checkIfFinished()
                }

                override fun onError(e: Throwable) {
                    chatRoomsListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, false)) }
                    checkIfFinished()
                }
            })
    }

    private fun checkAttachmentsHealth() {
        val startTime = System.currentTimeMillis()

        smsManagementRepository.testAttachmentApi()
            .timeout(checkTimeOutSeconds, TimeUnit.SECONDS, Single.just(false))
            .observeOn(schedulerProvider.ui())
            .subscribe(object : DisposableSingleObserver<Boolean>() {
                override fun onSuccess(t: Boolean) {
                    attachmentsListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, true)) }
                    checkIfFinished()
                }

                override fun onError(e: Throwable) {
                    attachmentsListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, false)) }
                    checkIfFinished()
                }
            })
    }

    private fun recordException(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    private fun clearResults() {
        sipRegistrationFinished = false
        sipDeregistrationFinished = false
        xmppConnectionFinished = false

        voicePushListItem.clearResult?.let { it() }
        chatPushListItem.clearResult?.let { it() }
        nextivaOnePushListItem.clearResult?.let { it() }
        sipRegistrationListItem.clearResult?.let { it() }
        sipDeregistrationListItem.clearResult?.let { it() }
        xmppConnectionListItem.clearResult?.let { it() }
        smsApiListItem.clearResult?.let { it() }
        vmTranscriptionApiListItem.clearResult?.let { it() }
        smsV2ListItem.clearResult?.let { it() }
        contactsListItem.clearResult?.let { it() }
        presenceListItem.clearResult?.let { it() }
        phoneNumberOrchestrationListItem.clearResult?.let { it() }
        loggingServiceListItem.clearResult?.let { it() }
        meetingsListItem.clearResult?.let { it() }
        chatRoomsListItem.clearResult?.let { it() }
        attachmentsListItem.clearResult?.let { it() }
    }

    private fun initRxEventListeners() {
        mCompositeDisposable.addAll(
                RxBus.listen(RxEvents.XmppPingEvent::class.java)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribe { event ->
                            xmppConnectionFinished = true
                            xmppConnectionListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - (xmppTextConnectionStartTime ?: System.currentTimeMillis()), event.isSuccessful)) }
                            checkIfFinished()
                        },
                RxBus.listen(RxEvents.SipRegisterFinished::class.java).subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribe { event ->
                            sipRegistrationFinished = true
                            sipDeregistrationStartTime = event.unregisterStartTime
                            sipManager.stopStackIfNecessary()

                            sipRegistrationStartTime?.let { startTime ->
                                sipRegistrationListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, event.isSuccessful)) }
                                checkIfFinished()
                            }

                            sipRegistrationStartTime = null
                        },
                RxBus.listen(RxEvents.SipDeregisterFinished::class.java).subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribe { event ->
                            xmppConnectionFinished = true
                            sipDeregistrationStartTime?.let { startTime ->
                                sipDeregistrationListItem.updateResult?.let { it(Pair(System.currentTimeMillis() - startTime, event.isSuccessful)) }
                                checkIfFinished()
                            }

                            sipDeregistrationStartTime = null
                        })
    }
}