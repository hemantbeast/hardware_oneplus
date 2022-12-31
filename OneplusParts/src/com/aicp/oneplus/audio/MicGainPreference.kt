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
package com.aicp.oneplus.audio;

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.SeekBar
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

import com.aicp.oneplus.OneplusParts
import com.aicp.oneplus.Utils
import com.aicp.oneplus.R

class MicGainPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs),
    OnSeekBarChangeListener {
    private var mMinValue = 0
    private var mMaxValue = 1
    private var mSeekBar: SeekBar? = null
    private var mOldStrength = 0

    init {
        // from sound/soc/codecs/wcd9335.c
        mFileName = context.resources.getString(R.string.pathAudioMicGain)
        if (isSupported) {
            mMinValue = context.resources.getInteger(R.integer.audioMicGainMin)
            mMaxValue = context.resources.getInteger(R.integer.audioMicGainMax)
        }
        DEFAULT_VALUE = getDefaultValue(context)
        layoutResource = R.layout.preference_seek_bar
        restore(context)
    }

    fun supported(): Boolean {
        return isSupported
    }

    fun getValue(context: Context?): String {
        return Utils.getFileValue(mFileName, DEFAULT_VALUE)
    }

    private fun setValue(newValue: String) {
        Log.d(TAG, "setValue - mFileName $mFileName - newValue $newValue")
        Utils.writeValue(mFileName, newValue)
        Settings.System.putString(context.contentResolver, SETTINGS_KEY, newValue)
    }

    private fun restore(context: Context) {
        if (!isSupported) {
            return
        }
        var storedValue = Settings.System.getString(context.contentResolver, SETTINGS_KEY)
        if (storedValue == null) {
            storedValue = DEFAULT_VALUE
        }
        Utils.writeValue(mFileName, storedValue)
    }

    override fun onProgressChanged(
        seekBar: SeekBar, progress: Int,
        fromTouch: Boolean
    ) {
        setValue((progress + mMinValue).toString())
        Log.d(TAG, "onProgressChanged - progress $progress - mMinValue $mMinValue")
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        mOldStrength = getValue(context).toInt()
        mSeekBar = holder.findViewById(R.id.seekbar) as SeekBar
        mSeekBar!!.max = mMaxValue - mMinValue
        mSeekBar!!.progress = mOldStrength - mMinValue
        mSeekBar!!.setOnSeekBarChangeListener(this)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // NA
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // NA
    }

    companion object {
        private const val TAG = "MicGainPreference"
        var SETTINGS_KEY = OneplusParts.KEY_SETTINGS_PREFIX + OneplusParts.KEY_MIC_GAIN
        lateinit var DEFAULT_VALUE: String

        private var mFileName: String? = null
        val isSupported: Boolean
            get() = if (mFileName != null && mFileName!!.isNotEmpty()) {
                Utils.fileWritable(mFileName)
            } else false

        fun getFile(context: Context): String? {
            mFileName = context.resources.getString(R.string.pathAudioMicGain)
            return if (isSupported) {
                mFileName
            } else null
        }

        fun getDefaultValue(context: Context): String {
            return if (isSupported) {
                context.resources.getInteger(R.integer.audioMicGainDefault)
                    .toString()
            } else {
                "0"
            }
        }
    }
}
