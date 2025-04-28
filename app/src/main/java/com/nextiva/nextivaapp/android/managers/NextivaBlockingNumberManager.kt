package com.nextiva.nextivaapp.android.managers

import android.app.Application
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository
import com.nextiva.nextivaapp.android.managers.interfaces.BlockingNumberManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.net.platform.CurrentSelectiveCallRejectionConditions
import com.nextiva.nextivaapp.android.models.net.platform.SelectiveCallRejection
import com.nextiva.nextivaapp.android.util.CallUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NextivaBlockingNumberManager @Inject constructor(
    val nextivaApplication: Application,
    val platformRepository: PlatformRepository,
    val sessionManager: SessionManager
) : BlockingNumberManager {

    private var blockedNumbers: MutableList<String> = mutableListOf()

    private val _blockedNumbersFlow = MutableStateFlow<List<String>>(emptyList())
    private val blockedNumbersFlow: StateFlow<List<String>> get() = _blockedNumbersFlow

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    init {
        fetchBlockedNumbers()
    }

    private fun fetchBlockedNumbers() {
        val sessionId = sessionManager.sessionId ?: return
        val userUUID = sessionManager.userInfo?.comNextivaUseruuid ?: return
        val accountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber ?: return

        scope.launch {
            platformRepository
                .fetchBlockedNumbers(accountNumber.toString(), sessionId, userUUID)
                .collectLatest { numbers ->
                    blockedNumbers = numbers?.toMutableList() ?: mutableListOf()
                    _blockedNumbersFlow.value = blockedNumbers.toList()
                }
        }
    }

    override fun refreshListFromWebSocket(numbers: List<String>) {
        blockedNumbers = numbers.toMutableList()
        _blockedNumbersFlow.value = blockedNumbers.toList()
    }

    override fun observeBlockedNumbers(): Flow<List<String>> = blockedNumbersFlow

    override fun isNumberBlocked(phoneNumber: String?): Boolean {
        if (phoneNumber.isNullOrEmpty() || phoneNumber == "null") {
            return false
        }
        val number = CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(phoneNumber)
        return blockedNumbers.contains(number)
    }

    override fun areAllNumbersForContactBlocked(numbers: List<PhoneNumber>?): Boolean {
        val phoneNumbers = numbers ?: return false
        if (phoneNumbers.isEmpty()) return false

        // more than 1 phone number
        var atLeastOneNumberIsUnblocked = false
        phoneNumbers.forEach { phone ->
            if (!isNumberBlocked(phone.strippedNumber)) {
                atLeastOneNumberIsUnblocked = true
                return@forEach
            }
        }
        return !atLeastOneNumberIsUnblocked
    }

    override fun resetBlockingNumberList(
        accountNumber: String,
        sessionId: String,
        userUUID: String
    ): Flow<Boolean?> {
        return platformRepository.blockOrUnblockNumber(
            accountNumber = accountNumber,
            sessionId = sessionId,
            userUUID = userUUID,
            setting = getDefaultSetting()
        )
    }

    private fun getDefaultSetting(): SelectiveCallRejection {
        return SelectiveCallRejection(
            currentSelectiveCallRejectionConditions = arrayListOf()
        )
    }

    private fun getSetting(phoneNumber: String, willBlock: Boolean): SelectiveCallRejection {
        val number = CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(phoneNumber)
        val updatedList = blockedNumbers.toMutableList()

        if (willBlock) {
            updatedList.addIfAbsent(number)
        } else {
            updatedList.remove(number)
        }

        // empty list -> reset on server
        if (updatedList.isEmpty()) {
            return getDefaultSetting()
        }

        val setting = SelectiveCallRejection(
            currentSelectiveCallRejectionConditions = arrayListOf(
                CurrentSelectiveCallRejectionConditions(
                    description = nextivaApplication.getString(
                        R.string.connect_call_details_block_number_description,
                        updatedList.firstOrNull()
                    ),
                    numbers = ArrayList(updatedList)
                )
            )
        )

        return setting
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun blockOrUnblockNumber(
        phoneNumber: String,
        willBlock: Boolean,
        accountNumber: String,
        sessionId: String,
        userUUID: String
    ): Flow<Boolean?> {
        return resetBlockingNumberList(accountNumber, sessionId, userUUID)
            .flatMapConcat {
                platformRepository.blockOrUnblockNumber(
                    accountNumber = accountNumber,
                    sessionId = sessionId,
                    userUUID = userUUID,
                    setting = getSetting(phoneNumber, willBlock)
                ).map { success ->
                    success?.let { handleBlockingState(phoneNumber, willBlock) }
                    success
                }
            }
    }

    override fun blockOrUnblockNumber(
        phoneNumber: String,
        willBlock: Boolean,
        accountNumber: String,
        sessionId: String,
        userUUID: String,
        onResult: (BlockingState) -> Unit
    ) {
        scope.launch {
            blockOrUnblockNumber(phoneNumber, willBlock, accountNumber, sessionId, userUUID)
                .collectLatest { success ->
                    val newState = BlockingResult
                        .from(success, !willBlock)
                        .toBlockingState()

                    withContext(Dispatchers.Main) {
                        onResult(newState)
                    }
                }
        }
    }

    private fun handleBlockingState(phoneNumber: String, willBlock: Boolean) {
        if (willBlock) {
            blockedNumbers.addIfAbsent(phoneNumber)
        } else {
            blockedNumbers.remove(phoneNumber)
        }
        _blockedNumbersFlow.value = blockedNumbers.toList()
    }

    private fun MutableList<String>.addIfAbsent(item: String) {
        if (!contains(item)) add(item)
    }
}

enum class BlockingState(val messageResId: Int) {
    Blocked(R.string.connect_call_details_block_number_success),
    Unblocked(R.string.connect_call_details_unblock_number_success),
    FailureBlocking(R.string.connect_call_details_block_number_failure),
    FailureUnblocking(R.string.connect_call_details_unblock_number_failure)
}

sealed class BlockingResult {
    data class Success(val wasBlocked: Boolean) : BlockingResult()
    data class Failure(val wasBlocked: Boolean) : BlockingResult()

    companion object {
        fun from(success: Boolean?, isBlocked: Boolean): BlockingResult {
            return if (success == true) Success(isBlocked) else Failure(isBlocked)
        }
    }
}

fun BlockingResult.toBlockingState(): BlockingState = when (this) {
    is BlockingResult.Success -> if (wasBlocked) BlockingState.Unblocked else BlockingState.Blocked
    is BlockingResult.Failure -> if (wasBlocked) BlockingState.FailureUnblocking else BlockingState.FailureBlocking
}
