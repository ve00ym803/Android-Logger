package com.android.logger.models

import com.android.logger.models.utils.DEFAULT_FILE_LOG_ENABLED
import com.android.logger.models.utils.DEFAULT_FILE_NAME
import com.android.logger.models.utils.DEFAULT_MAX_FILE_SIZE
import com.android.logger.models.utils.DEFAULT_PACKAGE_NAME
import com.android.logger.models.utils.DEFAULT_SYSTEM_LOG_ENABLED

class LogModel private constructor() {

    private var loggerBaseDirectory: String? = null
    private var isFileLogEnabled: Boolean = DEFAULT_FILE_LOG_ENABLED
    private var isSystemLogEnabled: Boolean = DEFAULT_SYSTEM_LOG_ENABLED
    private var maxFileSize: Int = DEFAULT_MAX_FILE_SIZE
    private var packageName: String = DEFAULT_PACKAGE_NAME
    private var fileName: String = DEFAULT_FILE_NAME
    private var disposalMethod: LogDisposalMethod? = null

    companion object {
        fun builder() = LogModel()
    }

    fun setLoggerBaseDirectory(directory: String) = apply { this.loggerBaseDirectory = directory }
    fun setLoggerIsFileLogEnabled(enabled: Boolean) = apply { this.isFileLogEnabled = enabled }
    fun setLoggerIsSystemLogEnabled(enabled: Boolean) = apply { this.isSystemLogEnabled = enabled }
    fun setLoggerMaxFileSize(size: Int) = apply { this.maxFileSize = size }
    fun setLoggerPackageName(packageName: String) = apply { this.packageName = packageName }
    fun setLoggerFileName(fileName: String) = apply { this.fileName = fileName }
    fun setLoggerDisposalMethod(method: LogDisposalMethod) = apply { this.disposalMethod = method }

    fun build(): LogConfiguration {

        return LogConfiguration(
            loggerBaseDirectory = loggerBaseDirectory ?: throw IllegalArgumentException("Logger base directory must not be null"),
            isFileLogEnabled = isFileLogEnabled,
            isSystemLogEnabled = isSystemLogEnabled,
            maxFileSize = maxFileSize,
            packageName = packageName,
            fileName = fileName,
            disposalMethod = disposalMethod ?: throw IllegalArgumentException("Disposal Method must not be null")
        )
    }
}
