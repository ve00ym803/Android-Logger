package com.android.logger.models

import com.android.logger.utils.MIN_FILE_DISPOSAL_BY_ARCHIVE_DAYS_COUNT
import com.android.logger.utils.MIN_FILE_DISPOSAL_BY_DELETION_DAYS_COUNT

sealed class LogDisposalMethod(
    var disposeAfter: Int,
    var disposeLocation: String? = null
) {
    class DisposeLogsByDeletion(logExpiryDays: Int) : LogDisposalMethod(
        disposeAfter = if (logExpiryDays < MIN_FILE_DISPOSAL_BY_DELETION_DAYS_COUNT) MIN_FILE_DISPOSAL_BY_DELETION_DAYS_COUNT else logExpiryDays
    )

    class DisposeLogsByArchive(logExpiryDays: Int, archiveLocation: String) : LogDisposalMethod(
        disposeAfter = if (logExpiryDays < MIN_FILE_DISPOSAL_BY_ARCHIVE_DAYS_COUNT) MIN_FILE_DISPOSAL_BY_ARCHIVE_DAYS_COUNT else logExpiryDays,
        disposeLocation = archiveLocation
    )
}

