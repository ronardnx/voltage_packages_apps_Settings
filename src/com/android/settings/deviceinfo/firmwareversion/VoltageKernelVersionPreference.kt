/*
 * Copyright (C) 2026 VoltageOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo.firmwareversion

import android.content.Context
import android.util.Log
import androidx.preference.Preference
import com.android.settings.R
import com.android.settingslib.DeviceInfoUtils
import com.android.settingslib.datastore.KeyValueStore
import com.android.settingslib.metadata.PersistentPreference
import com.android.settingslib.metadata.PreferenceMetadata
import com.android.settingslib.metadata.PreferenceSummaryProvider
import com.android.settingslib.metadata.SensitivityLevel
import com.android.settingslib.preference.PreferenceBinding
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

class VoltageKernelVersionPreference :
    PersistentPreference<String>, PreferenceMetadata, PreferenceSummaryProvider, PreferenceBinding,
    Preference.OnPreferenceClickListener {

    override val key: String
        get() = "kernel_version"

    override val purpose: Int
        get() = R.string.kernel_version_purpose

    override val title: Int
        get() = R.string.kernel_version

    override val supportsWrite = false

    override val valueType = String::class.javaObjectType

    override fun storage(context: Context): KeyValueStore = createSummaryStorage(context, key)

    override fun getSummary(context: Context): CharSequence? =
        DeviceInfoUtils.getFormattedKernelVersion(context)

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.isSelectable = true
        preference.isCopyingEnabled = true
        preference.onPreferenceClickListener = this
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        showingFullVersion = !showingFullVersion
        preference.summary =
            if (showingFullVersion) fullKernelVersion
            else DeviceInfoUtils.getFormattedKernelVersion(preference.context)
        return true
    }

    override val sensitivityLevel
        get() = SensitivityLevel.NO_SENSITIVITY

    private var showingFullVersion = false

    private val fullKernelVersion: String
        get() = try {
            readLine(FILENAME_PROC_VERSION) ?: UNAVAILABLE
        } catch (e: IOException) {
            Log.e(LOG_TAG, "IO Exception when getting kernel version for Device Info screen", e)
            UNAVAILABLE
        }

    private companion object {
        const val FILENAME_PROC_VERSION = "/proc/version"
        const val UNAVAILABLE = "Unavailable"
        const val LOG_TAG = "VoltageKernelVersionPreference"

        @Throws(IOException::class)
        fun readLine(filename: String): String? =
            BufferedReader(FileReader(filename), 256).use { it.readLine() }
    }
}
