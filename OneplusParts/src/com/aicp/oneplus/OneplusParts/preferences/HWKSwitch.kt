package com.aicp.oneplus.OneplusParts.preferences

import android.content.Context
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener

import com.aicp.oneplus.OneplusParts.Constants
import com.aicp.oneplus.OneplusParts.R;
import com.aicp.oneplus.OneplusParts.Utils


class HWKSwitch(context: Context) : OnPreferenceChangeListener {
    private var mContext: Context

    init {
        mContext = context
        mFileName = context.resources.getString(R.string.pathHWKToggle)
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        val enabled = newValue as Boolean
        Utils.writeValue(getFile(mContext), if (enabled) "1" else "0")
        Settings.System.putString(mContext.contentResolver, SETTINGS_KEY, if (enabled) "1" else "0")
        return true
    }

    companion object {
        var SETTINGS_KEY = Constants.KEY_SETTINGS_PREFIX + Constants.KEY_HWK_SWITCH
        private var mFileName: String? = null

        val isSupported: Boolean
            get() = if (mFileName != null && mFileName!!.isNotEmpty()) {
                Utils.fileWritable(mFileName)
            } else false

        fun getFile(context: Context): String? {
            mFileName = context.resources.getString(R.string.pathHWKToggle)
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
            val storedValue = Settings.System.getString(context.contentResolver, SETTINGS_KEY)
            Utils.writeValue(mFileName, storedValue)
        }
    }
}