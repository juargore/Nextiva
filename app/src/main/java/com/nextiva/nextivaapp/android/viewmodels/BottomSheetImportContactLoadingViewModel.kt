package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.SMSMessages.ExternalSourceType
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.models.BulkContacts
import com.nextiva.nextivaapp.android.models.BulkContactsResult
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContact
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactExternalSource
import com.nextiva.nextivaapp.android.util.extensions.orZero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomSheetImportContactLoadingViewModel @Inject constructor(
    private val application: Application,
    private val contactsRepository: PlatformContactsRepository,
    private val mLogManager: LogManager,
    private val mConnectionStateManager: ConnectionStateManager
) : ViewModel() {

    sealed class State {
        class Loading(val size: Int = 1) : State()
        class Error(val error: String? = null) : State()
        class Success(val bulkContactsResult: BulkContactsResult,
                      var finished: Boolean = false
        ) : State()
    }


    enum class BulkStatusType(val value: String) {
        CREATED("created"),
        INCOMPLETE("incomplete"),
        WAITING_FOR_DATA("waiting-for-data"),
        PROCESSING("processing"),
        TIMED_OUT("timed-out"),
        FAILED("failed"),
        SUCCESS("success"),
        PARTIAL_SUCCESS("partial-success"),
        CANCELLED("cancelled");
    }

    private val _state = MutableStateFlow<State>(State.Loading(1))
    val state: StateFlow<State> = _state


    fun startContactImport(
        contactsList: List<NextivaContact>,
        duplicatesStrategy: String
    ) {

        viewModelScope.launch(context = Dispatchers.IO) {

            if (contactsList.isEmpty() || duplicatesStrategy.isBlank()) {
                // cannot continue if data is empty
                _state.emit(State.Error(null))
                mLogManager.logToFile(Enums.Logging.STATE_ERROR, "startContactImport -> data is empty! -> contactsList = $contactsList && duplicatesStrategy = $duplicatesStrategy")
                return@launch
            }

            if (!mConnectionStateManager.isInternetConnected) {
                _state.emit(State.Error(application.getString(R.string.error_no_internet_import_contact)))
                return@launch
            }

            _state.emit(State.Loading(contactsList.size.orZero()))

            contactsRepository.bulkExternalContactImportRequest(
                BulkContacts(
                    contacts = contactsList.map { ConnectContact(it).also { contact ->
                        contact.externalSource = ConnectContactExternalSource(
                            externalId = contact.externalId,
                            sourceIdentity = contact.externalId,
                            type = ExternalSourceType.USER
                        )
                    }},
                    duplicateUpdateStrategyType = duplicatesStrategy
                )
            )?.let {
                val thousandContacts = (contactsList.size / 1000).coerceAtLeast(1).toLong()
                pollResults(it.id, thousandContacts)
            } ?: run {
                mLogManager.logToFile(Enums.Logging.STATE_ERROR, "bulkExternalContactImportRequest null")
                _state.emit(State.Error())
            }
        }
    }

    fun finishAnimation() {
        (_state.value as? State.Success)?.let {
            it.finished = true
        }
    }

    private suspend fun pollResults(jobId: String, thousands: Long) {
        var timeLimit = thousands * 10
        val interval = 1000L
        while (timeLimit-- > 0) {
            contactsRepository.bulkExternalContactImportRequestState(jobId)
                ?.let { bulkContactResult ->
                    when (BulkStatusType.entries.firstOrNull {
                        it.value.equals(
                            bulkContactResult.result?.status,
                            ignoreCase = true
                        )
                    }) {
                        BulkStatusType.PARTIAL_SUCCESS,
                        BulkStatusType.SUCCESS -> {
                            fetchContacts(bulkContactResult)
                            return
                        }
                        BulkStatusType.FAILED -> {
                            _state.emit(State.Error())
                            return
                        }
                        else -> {
                            // do not do anything, BackEnd process is still going on
                        }
                    }
                }

            delay(interval)
        }
        _state.emit(State.Error())
    }

    private suspend fun fetchContacts(bulkContactsResult: BulkContactsResult) {
        contactsRepository.fetchContacts(false, {
            viewModelScope.launch {
                _state.emit(State.Success(bulkContactsResult))
            }
        }, {})
    }
}