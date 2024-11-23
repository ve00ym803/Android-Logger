package com.android.sampleApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.logger.Logger
import com.android.logger.models.LogConfiguration
import com.android.logger.models.LogDisposalMethod
import com.android.logger.models.LogModel

class MainActivity : AppCompatActivity() {

    private val logTag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLogger()
    }

    private fun initLogger() {

        val mediaDirs = application.externalMediaDirs
        val internalFilesDir = application.filesDir
        val loggerPath = "/logger_" + BuildConfig.VERSION_NAME

        val logConfiguration = LogModel.builder()
            .setLoggerBaseDirectory(
                if (!mediaDirs.isNullOrEmpty()) mediaDirs[0].absolutePath + loggerPath else internalFilesDir.absolutePath + loggerPath
            )
            .setLoggerIsFileLogEnabled(true)
            .setLoggerIsSystemLogEnabled(true)
            .setLoggerMaxFileSize(1024)
            .setLoggerPackageName(BuildConfig.APPLICATION_ID)
            .setLoggerFileName("log")
            .setLoggerDisposalMethod(
                LogDisposalMethod.DisposeLogsByArchive(
                    logExpiryDays = 14,
                    archiveLocation = application.filesDir.absolutePath + "/logBackUp"
                )
            )
            .build()

        val lgr = Logger(this@MainActivity)
        lgr.setConfigurations(logConfiguration)

        lgr.e("$logTag-LoggerTest", "Logger initialized")

    }
}