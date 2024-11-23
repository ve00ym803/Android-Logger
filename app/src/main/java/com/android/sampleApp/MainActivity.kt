package com.android.sampleApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.logger.models.LogConfiguration
import com.android.logger.models.LogDisposalMethod
import com.android.logger.models.LogModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLogger()
    }

    private fun initLogger(){

        val mediaDirs = application.externalMediaDirs
        val internalFilesDir = application.filesDir
        val loggerPath = "/logger_" + BuildConfig.VERSION_NAME

        LogModel.builder()
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
    }
}