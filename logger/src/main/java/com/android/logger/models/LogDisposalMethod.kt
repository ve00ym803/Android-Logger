package com.android.logger.models

sealed class LogDisposalMethod(
    val disposeAfter: Int,
    val disposeLocation: String? = null
) {
    class DisposeLogsByArchive(
        logExpiryDays: Int,
        archiveLocation: String
    ) : LogDisposalMethod(logExpiryDays, archiveLocation)

    class DisposeLogsByDeletion(
        logExpiryDays: Int
    ) : LogDisposalMethod(logExpiryDays)
}
