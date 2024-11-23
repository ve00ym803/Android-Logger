package com.android.logger.models

data class LogConfiguration (
    val baseDirectory: String,
    val isFileLogEnabled: Boolean,
    val isSystemLogEnabled: Boolean,
    val maxFileSize: Int,
    val packageName: String,
    val fileName: String,
    val disposalMethod: LogDisposalMethod
)