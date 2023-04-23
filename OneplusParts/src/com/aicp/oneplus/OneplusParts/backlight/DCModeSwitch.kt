package com.aicp.oneplus.OneplusParts.backlight

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceManager

import com.aicp.oneplus.OneplusParts.OneplusParts
import com.aicp.oneplus.OneplusParts.R
import com.aicp.oneplus.OneplusParts.Utils


class DCModeSwitch(context: Context) : OnPreferenceChangeListener {
    private var mContext: Context

    init {
        mContext = context
        mFileName = context.resources.getString(R.string.pathBacklightDimmer)
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        val enabled = newValue as Boolean
        Settings.System.putInt(mContext.contentResolver, SETTINGS_KEY, if (enabled) 1 else 0)
        Utils.writeValue(getFile(mContext), if (enabled) "1" else "0")
        return true
    }

    companion object {
        var SETTINGS_KEY = OneplusParts.KEY_SETTINGS_PREFIX + OneplusParts.KEY_BACKLIGHT
        private var mFileName: String? = null

        val isSupported: Boolean
            get() = if (mFileName != null && mFileName!!.isNotEmpty()) {
                Utils.fileWritable(mFileName)
            } else false

        fun getFile(context: Context): String? {
            mFileName = context.resources.getString(R.string.pathBacklightDimmer)
            return if (isSupported) {
                mFileName
            } else null
        }

        fun isCurrentlyEnabled(context: Context): Boolean {
            return Utils.getFileValueAsBoolean(getFile(context), false)
        }

        fun restore(context: Context) {
            if (!isSupported) {
                return
            }

            val storedValue = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_KEY, "0")
            Utils.writeValue(getFile(context), storedValue!!)

            // Oneplus 5/5t configuration
            if (Build.DEVICE.equals("OnePlus5")) {
                Utils.writeValue("/proc/flicker_free/min_brightness", "66")
            } else if (Build.DEVICE.equals("OnePlus5T")) {
                Utils.writeValue("/proc/flicker_free/min_brightness", "302")
            }
        }
    }
}