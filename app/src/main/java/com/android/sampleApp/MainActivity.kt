package com.android.sampleApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.android.logger.Logger
import com.android.logger.models.LogDisposalMethod
import com.android.logger.models.LogModel

class MainActivity : AppCompatActivity() {

    private val logTag = "MainActivity"

    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLogger()
    }

    private fun initLogger() {

        val mediaDirs = application.externalMediaDirs
        val internalFilesDir = application.filesDir
        val loggerPath = "/logger_" + BuildConfig.VERSION_NAME + "_" + com.android.logger.BuildConfig.LIBRARY_VERSION

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

        val handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            lgr.d("$logTag-LoggerTest", "This is a debug log")
            handler.postDelayed(runnable, 50)
        }

        handler.post(runnable)
    }
}