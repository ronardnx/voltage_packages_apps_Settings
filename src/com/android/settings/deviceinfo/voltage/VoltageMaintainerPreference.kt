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

package com.android.settings.deviceinfo.voltage

import android.content.Context
import android.os.SystemProperties
import androidx.preference.Preference
import com.android.settings.R
import com.android.settingslib.metadata.PreferenceMetadata
import com.android.settingslib.metadata.PreferenceSummaryProvider
import com.android.settingslib.preference.PreferenceBinding

class VoltageMaintainerPreference :
    PreferenceMetadata, PreferenceSummaryProvider, PreferenceBinding {

    override val key: String
        get() = "voltage_maintainer"

    override val title: Int
        get() = R.string.voltage_maintainer_title

    override val purpose: Int
        get() = title

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.isCopyingEnabled = true
    }

    override fun getSummary(context: Context): CharSequence? {
        val buildStatus = getBuildStatus(context)
        if (buildStatus != context.getString(R.string.unknown)) {
            return "$buildStatus by ${context.getString(R.string.voltage_maintainer)}"
        }
        return context.getString(R.string.unknown)
    }

    private fun getBuildStatus(context: Context): String {
        val buildStatus = SystemProperties.get(BUILD_STATUS_PROPERTY, "")
        if (buildStatus.equals("OFFICIAL", ignoreCase = true) ||
            buildStatus.equals("UNOFFICIAL", ignoreCase = true)) {
            return buildStatus
        }
        return context.getString(R.string.unknown)
    }

    companion object {
        const val BUILD_STATUS_PROPERTY: String = "ro.voltage.build.status"
    }
}
