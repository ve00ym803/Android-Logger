package com.android.logger.worker

import android.content.Context
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.android.logger.models.LogDisposalMethod
import com.android.logger.utils.FileDisposer
import com.android.logger.utils.TYPE_ARCHIVE
import com.android.logger.utils.TYPE_DELETE
import com.android.logger.utils.WORKER_BASE_LOCATION_KEY
import com.android.logger.utils.WORKER_DISPOSE_LOCATION_KEY
import com.android.logger.utils.WORKER_DISPOSE_TIME_IN_DAYS_KEY
import com.android.logger.utils.WORKER_JOB_TAG
import com.android.logger.utils.WORKER_REPETITION_FREQUENCY
import com.android.logger.utils.WORKER_REPETITION_TIME_UNIT
import com.android.logger.utils.WORKER_TASK_TYPE_KEY

object WorkerManager {

    fun initializeWorkManager(context: Context) {
        val configuration = Configuration.Builder().build()
        WorkManager.initialize(context, configuration)
    }

    fun enqueueLogCleanupWorker(
        context: Context,
        logBaseLocation: String,
        logDisposalMethod: LogDisposalMethod
    ) {

        val taskType = when (logDisposalMethod) {
            is LogDisposalMethod.DisposeLogsByDeletion -> TYPE_DELETE
            is LogDisposalMethod.DisposeLogsByArchive -> TYPE_ARCHIVE
        }

        val workerData = Data.Builder()
            .putInt(WORKER_TASK_TYPE_KEY, taskType)
            .putString(WORKER_BASE_LOCATION_KEY, logBaseLocation)
            .putInt(WORKER_DISPOSE_TIME_IN_DAYS_KEY, logDisposalMethod.disposeAfter)
            .putString(WORKER_DISPOSE_LOCATION_KEY, logDisposalMethod.disposeLocation)
            .build()

        val logCleanupRequest = PeriodicWorkRequest.Builder(
            FileDisposer::class.java,
            WORKER_REPETITION_FREQUENCY,
            WORKER_REPETITION_TIME_UNIT,
        )
        .setInputData(workerData)
        .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(WORKER_JOB_TAG, ExistingPeriodicWorkPolicy.KEEP, logCleanupRequest)
    }
}
