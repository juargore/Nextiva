package com.nextiva.nextivaapp.android.managers.interfaces

import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.managers.BlockingState
import kotlinx.coroutines.flow.Flow

interface BlockingNumberManager {

    fun refreshListFromWebSocket(numbers: List<String>)

    fun isNumberBlocked(phoneNumber: String?): Boolean

    fun areAllNumbersForContactBlocked(numbers: List<PhoneNumber>?): Boolean

    fun resetBlockingNumberList(
        accountNumber: String,
        sessionId: String,
        userUUID: String
    ) : Flow<Boolean?>

    fun blockOrUnblockNumber(
        phoneNumber: String,
        willBlock: Boolean,
        accountNumber: String,
        sessionId: String,
        userUUID: String,
        onResult: (BlockingState) -> Unit
    )

    fun observeBlockedNumbers(): Flow<List<String>>
}
