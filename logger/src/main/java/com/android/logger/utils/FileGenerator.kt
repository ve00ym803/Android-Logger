package com.android.logger.utils

import android.content.SharedPreferences
import com.android.logger.models.LogConfiguration
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileGenerator(
    private val logConfiguration: LogConfiguration?,
    private val preferences: SharedPreferences
) {
    private var lastFileDate: String? = null
    private val fileDateFormat = SimpleDateFormat("dd_MM_yy", Locale.getDefault())

    fun getWritableLogFile(): File? {
        val currentDateValue = fileDateFormat.format(Date())
        val preferenceDefaultValue = 0

        if (lastFileDate == null) {
            lastFileDate = currentDateValue
        }
        else if (currentDateValue != lastFileDate) {
            lastFileDate = currentDateValue
            setPreferenceValue(preferenceDefaultValue)
        }

        val logFile = File("${logConfiguration?.baseDirectory}/${logConfiguration?.fileName}_${lastFileDate}_${preferences.getInt(logConfiguration?.fileName.orEmpty(), preferenceDefaultValue)}.txt")

        return if (!logFile.exists()) {
            if (logFile.createNewFile()) logFile else null
        }
        else {

            val fileSizeInKB = logFile.length() / 1024

            if (fileSizeInKB >= (logConfiguration?.maxFileSize ?: 0)) {

                val updatedPreferenceValue = preferences.getInt(logConfiguration?.fileName.orEmpty(), preferenceDefaultValue) + 1
                setPreferenceValue(updatedPreferenceValue)

                val updatedLogFile = File(
                    "${logConfiguration?.baseDirectory}/${logConfiguration?.fileName}_${lastFileDate}_$updatedPreferenceValue.txt"
                )

                if (!updatedLogFile.exists()) {
                    if (updatedLogFile.createNewFile()) {
                        updatedLogFile
                    }
                    else null
                }
                else updatedLogFile
            }
            else logFile
        }
    }

    private fun setPreferenceValue(preferenceValue: Int) {
        val editor = preferences.edit()
        editor.putInt(logConfiguration?.fileName.orEmpty(), preferenceValue)
        editor.apply()
    }
}
