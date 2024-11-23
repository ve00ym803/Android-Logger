package com.android.logger

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.logger.models.LogConfiguration
import com.android.logger.models.LogDisposalMethod
import com.android.logger.utils.FileGenerator
import com.android.logger.worker.WorkerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Logger(private val context: Context) {

    private val logTag = "Logger"
    private val simpleTimeFormat = SimpleDateFormat("yy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private var logConfiguration: LogConfiguration? = null
    private var logDisposalMethod: LogDisposalMethod? = null
    private var preferences: SharedPreferences? = null
    private var fileGenerator: FileGenerator? = null

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var coroutineDispatcher = Dispatchers.IO

    fun setConfigurations(configurations: LogConfiguration?) {

        if (configurations != null) {

            logConfiguration = configurations
            logDisposalMethod = configurations.disposalMethod

            generateDirectories()
        }
    }

    private fun generateDirectories() {
        logConfiguration?.let { config ->

            val logBaseDir = File(config.baseDirectory)

            val logBaseDirExists = if (!logBaseDir.exists()) {
                logBaseDir.mkdirs()
            }
            else true

            Log.w("$logTag : logBaseDir", "${logBaseDir.absolutePath}, baseDirExists => $logBaseDirExists")

            logDisposalMethod?.let { method ->
                WorkerManager.enqueueLogCleanupWorker(context, config.baseDirectory, method)
                if (method is LogDisposalMethod.DisposeLogsByArchive) {
                    method.disposeLocation?.let { disposeLocation ->
                        val logDisposeDir = File(disposeLocation)

                        val logDisposeDirExists = if (!logDisposeDir.exists()) {
                            logDisposeDir.mkdirs()
                        }
                        else true

                        Log.w("$logTag : logDisposeDir", "${logDisposeDir.absolutePath}, disposeDirExists => $logDisposeDirExists")
                    }
                }
            }

            if (logBaseDirExists) {
                preferences = context.getSharedPreferences(BuildConfig.LIBRARY_PACKAGE_NAME, Context.MODE_PRIVATE)
                fileGenerator = FileGenerator(config, preferences!!)
            }
        }
    }

    fun e(tag: String, data: String) {
        logConfiguration?.let {
            if (it.isSystemLogEnabled) Log.e(tag, data)
            if (it.isFileLogEnabled) appendLog("ERROR", "$tag => $data")
        }
    }

    fun w(tag: String, data: String) {
        logConfiguration?.let {
            if (it.isSystemLogEnabled) Log.w(tag, data)
            if (it.isFileLogEnabled) appendLog("WARN", "$tag => $data")
        }
    }

    fun i(tag: String, data: String) {
        logConfiguration?.let {
            if (it.isSystemLogEnabled) Log.i(tag, data)
            if (it.isFileLogEnabled) appendLog("INFO", "$tag => $data")
        }
    }

    fun d(tag: String, data: String) {
        logConfiguration?.let {
            if (it.isSystemLogEnabled) Log.d(tag, data)
            if (it.isFileLogEnabled) appendLog("DEBUG", "$tag => $data")
        }
    }

    fun v(tag: String, data: String) {
        logConfiguration?.let {
            if (it.isSystemLogEnabled) Log.v(tag, data)
            if (it.isFileLogEnabled) appendLog("VERBOSE", "$tag => $data")
        }
    }

    private fun appendLog(logType: String, data: String) = coroutineScope.launch (coroutineDispatcher) {
        try {
            fileGenerator?.let { generator ->
                generator.getWritableLogFile()?.let { logFile ->
                    BufferedWriter(FileWriter(logFile, true)).use { buf ->
                        buf.append(simpleTimeFormat.format(Date()))
                        buf.append(" : ")
                        buf.append(logConfiguration?.packageName.orEmpty())
                        buf.append(" : ")
                        buf.append(logType)
                        buf.append(" : ")
                        buf.append(data)
                        buf.newLine()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancel() {
        coroutineScope.cancel()
    }
}
