/*
* Copyright (C) 2016 The OmniROM Project
* Copyright (C) 2022 The Android Ice Cold Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.aicp.oneplus.OneplusParts.audio

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet

import com.aicp.oneplus.OneplusParts.Constants
import com.aicp.oneplus.OneplusParts.R
import com.aicp.oneplus.OneplusParts.preferences.CustomSeekBarPreference
import com.aicp.oneplus.OneplusParts.Utils

class EarpieceGainPreference(context: Context, attrs: AttributeSet?) : CustomSeekBarPreference(context, attrs) {

    init {
        // from sound/soc/codecs/wcd9335.c
        mFileName = context.resources.getString(R.string.pathAudioEarpieceGain)
        if (isSupported) {
            mInterval = context.resources.getInteger(R.integer.audioEarpieceInterval)
            mMinValue = context.resources.getInteger(R.integer.audioEarpieceMin)
            mMaxValue = context.resources.getInteger(R.integer.audioEarpieceMax)
            mShowSign = false
            mUnits = ""
            mContinuousUpdates = false
        }
        mDefaultValueExists = true
        mDefaultValue = getDefaultValue(context)
        DEFAULT_VALUE = mDefaultValue.toString()
        mValue = getValue(context).toInt()
        isPersistent = false
    }

    fun supported(): Boolean {
        return isSupported
    }

    fun getValue(context: Context?): String {
        return Utils.getFileValue(mFileName, DEFAULT_VALUE)
    }

    override fun changeValue(newValue: Int) {
        setValue(newValue.toString())
    }

    private fun setValue(newValue: String) {
        Utils.writeValue(mFileName, newValue)
        Settings.System.putString(context.contentResolver, SETTINGS_KEY, newValue)
    }

    companion object {
        var SETTINGS_KEY = Constants.KEY_SETTINGS_PREFIX + Constants.KEY_EARPIECE_GAIN

        private var mFileName: String? = null
        lateinit var DEFAULT_VALUE: String

        val isSupported: Boolean
            get() = if (mFileName != null && mFileName!!.isNotEmpty()) {
                Utils.fileWritable(mFileName)
            } else false

        fun getFile(context: Context): String? {
            mFileName = context.resources.getString(R.string.pathAudioEarpieceGain)
            return if (isSupported) {
                mFileName
            } else null
        }

        fun getDefaultValue(context: Context): Int {
            return if (isSupported) {
                context.resources.getInteger(R.integer.audioEarpieceDefault)
            } else {
                0
            }
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
