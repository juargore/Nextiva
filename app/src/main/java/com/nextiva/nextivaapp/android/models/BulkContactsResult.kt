package com.nextiva.nextivaapp.android.models

data class BulkContactsResult(
    val id: String,
    val corpAcctNumber: String?,
    val submittedBy: SubmittedBy?,
    val recordCount: Int?,
    val active: Boolean?,
    val jobType: String?,
    val jobSubType: String?,
    val webhook: String?,
    val createdAt: String?,
    val endedAt: String?,
    val metadata: Metadata?,
    val result: Result?,
) {

    data class Metadata(
        val duplicateUpdateStrategyType: String?
    )

    data class SubmittedBy(
        val firstName: String?,
        val lastName: String?,
        val userUuid: String?
    )

    data class ResultItem(
        val id: String,
        val errorMessage: String?,
        val externalId: String?,
        val operationType: String?,
        val success: Boolean?,
        val url: String?
    )

    data class Result(
        val status: String?,
        val jobType: String?,
        val jobSubType: String?,
        val submittedBy: SubmittedBy?,
        val processed: Int?,
        val inserted: Int?,
        val updated: Int?,
        val deleted: Int?,
        val exported: Int?,
        val eventSynced: Int?,
        val converted: Int?,
        val failed: Int?,
        val skipped: Int?,
        val received: Int?,
        val results: List<ResultItem>?,
    )
}
