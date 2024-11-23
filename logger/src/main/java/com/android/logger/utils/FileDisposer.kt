package com.android.logger.utils

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.collections.ArrayList

class FileDisposer(context: Context, params: WorkerParameters) : Worker(context, params) {

    private val logTag = "FileDisposer"
    private val fileDateFormat = SimpleDateFormat("dd_MM_yy", Locale.getDefault())

    override fun doWork(): Result {
        val taskType = inputData.getInt(WORKER_TASK_TYPE_KEY, TYPE_UNMATCHED)
        val baseLocation = inputData.getString(WORKER_BASE_LOCATION_KEY)
        val disposeInDaysCount = inputData.getInt(WORKER_DISPOSE_TIME_IN_DAYS_KEY, MIN_FILE_DISPOSAL_BY_ARCHIVE_DAYS_COUNT)
        val disposeLocation = inputData.getString(WORKER_DISPOSE_LOCATION_KEY)

        Log.w(
            logTag, "doWork: " +
                    "taskType => $taskType, " +
                    "baseLocation => $baseLocation, " +
                    "disposeInDays => $disposeInDaysCount, " +
                    "disposeLocation => $disposeLocation"
        )

        return if (baseLocation != null && disposeLocation != null){

            when (taskType) {

                TYPE_DELETE -> deleteOldFiles(baseLocation, disposeInDaysCount)

                TYPE_ARCHIVE -> archiveFilesAndMoveToBackup(baseLocation, disposeInDaysCount, disposeLocation)
            }

            if (taskType != TYPE_UNMATCHED) {
                Result.success()
            }
            else Result.failure()

        }
        else Result.failure()

    }

    private fun deleteOldFiles(baseLocation: String, disposeInDaysCount: Int) {
        try {
            val indexedFiles = getListOfOldFiles(baseLocation, disposeInDaysCount)
            if (indexedFiles.isNotEmpty()) {
                deleteFiles(indexedFiles)
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun archiveFilesAndMoveToBackup(baseLocation: String, disposeInDaysCount: Int, disposeLocation: String) {
        try {
            val indexedFiles = getListOfOldFiles(baseLocation, disposeInDaysCount)
            if (indexedFiles.isNotEmpty()) {
                val sourceZipFileName = "backup_${fileDateFormat.format(Date())}.zip"
                val sourceZipFilePath = "$baseLocation/$sourceZipFileName"
                val sourceZipFile = File(sourceZipFilePath)

                if (sourceZipFile.createNewFile()) {
                    FileOutputStream(sourceZipFile).use { fos ->
                        ZipOutputStream(fos).use { zos ->
                            indexedFiles.forEach { file ->
                                zos.putNextEntry(ZipEntry(file.name))
                                FileInputStream(file).use { inputStream ->
                                    inputStream.copyTo(zos)
                                }
                                zos.closeEntry()
                            }
                        }
                    }
                }

                if (moveToBackup(sourceZipFilePath, "$disposeLocation/$sourceZipFileName")) {
                    deleteFiles(indexedFiles)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getListOfOldFiles(baseLocation: String, disposeInDaysCount: Int): List<File> {
        val oldFilesList = ArrayList<File>()
        val currentTime = Calendar.getInstance().timeInMillis
        val timeDifference = disposeInDaysCount * 24 * 3600 * 1000L

        val folder = File(baseLocation)
        if (folder.exists() && folder.isDirectory) {
            folder.listFiles()?.forEach { fileEntry ->
                if (fileEntry.isFile && fileEntry.exists() && currentTime - fileEntry.lastModified() >= timeDifference) {
                    oldFilesList.add(fileEntry)
                }
            }
        }

        return oldFilesList
    }

    private fun moveToBackup(sourceFilePath: String, destinationFilePath: String): Boolean {
        val sourceFile = File(sourceFilePath)
        val destinationFile = File(destinationFilePath)

        FileInputStream(sourceFile).use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return sourceFile.delete()
    }

    private fun deleteFiles(indexedFiles: List<File>) {
        indexedFiles.forEach { file ->
            file.delete()
        }
    }
}
