package com.aicp.oneplus.OneplusParts.preferences

import android.content.Context
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import com.aicp.oneplus.OneplusParts.OneplusParts
import com.aicp.oneplus.OneplusParts.Utils
import com.aicp.oneplus.OneplusParts.R;


class HWKSwitch(context: Context) : OnPreferenceChangeListener {
    private var mContext: Context

    init {
        mContext = context
        mFileName = context.resources.getString(R.string.pathHWKToggle)
    }

    fun supported(): Boolean {
        return isSupported
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        val enabled = newValue as Boolean
        Settings.System.putInt(mContext.contentResolver, SETTINGS_KEY, if (enabled) 1 else 0)
        Utils.writeValue(getFile(mContext), if (enabled) "1" else "0")
        return true
    }

    companion object {
        private var mFileName: String? = null

        var SETTINGS_KEY = OneplusParts.KEY_SETTINGS_PREFIX + OneplusParts.KEY_HWK_SWITCH

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
    }
}