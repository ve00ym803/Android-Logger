package com.android.logger.models

class LogModel(
    private var loggerBaseDirectory: String? = null,
    private var isFileLogEnabled: Boolean = true,
    private var isSystemLogEnabled: Boolean = true,
    private var maxFileSize: Int = 1024 * 1024,
    private var packageName: String = "com.android.logger",
    private var fileName: String = "log",
    private var disposalMethod: LogDisposalMethod? = null
) {

    companion object {
        fun builder() = LogModel()
    }

    fun build(): LogModel {
        return LogModel(loggerBaseDirectory ?: throw IllegalArgumentException("Logger base directory must not be null"),
            isFileLogEnabled,
            isSystemLogEnabled,
            maxFileSize,
            packageName,
            fileName,
            disposalMethod ?: throw IllegalArgumentException("Disposal Method must not be null")
        )
    }

    fun setLoggerBaseDirectory(directory: String) = apply { this.loggerBaseDirectory = directory }
    fun setLoggerIsFileLogEnabled(enabled: Boolean) = apply { this.isFileLogEnabled = enabled }
    fun setLoggerIsSystemLogEnabled(enabled: Boolean) = apply { this.isSystemLogEnabled = enabled }
    fun setLoggerMaxFileSize(size: Int) = apply { this.maxFileSize = size }
    fun setLoggerPackageName(packageName: String) = apply { this.packageName = packageName }
    fun setLoggerFileName(fileName: String) = apply { this.fileName = fileName }
    fun setLoggerDisposalMethod(method: LogDisposalMethod) = apply { this.disposalMethod = method }
}
