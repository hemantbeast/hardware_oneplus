package com.aicp.oneplus.OneplusParts.preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.AttributeSet
import androidx.preference.PreferenceManager

import com.aicp.oneplus.OneplusParts.OneplusParts
import com.aicp.oneplus.OneplusParts.R
import com.aicp.oneplus.OneplusParts.Utils


class VibratorStrengthPreference(context: Context, attrs: AttributeSet?) : CustomSeekBarPreference(context, attrs) {
    private var mVibrator: Vibrator? = null

    init {
        mFileName = context.resources.getString(R.string.pathSystemVibStrength)

        if (isSupported) {
            mInterval = context.resources.getInteger(R.integer.vibratorInterval)
            mMinValue = context.resources.getInteger(R.integer.vibratorMinMV)
            mMaxValue = context.resources.getInteger(R.integer.vibratorMaxMV)
            mShowSign = false
            mUnits = ""
            mContinuousUpdates = false
        }

        mDefaultValueExists = true
        mDefaultValue = context.resources.getInteger(R.integer.vibratorDefaultMV)
        DEFAULT_VALUE = mDefaultValueText
        mValue = getValue(context).toInt()
        isPersistent = false

        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        mVibrator = vibratorManager.defaultVibrator
    }

    override fun changeValue(newValue: Int) {
        setValue(newValue.toString(), true)
    }

    fun setValue(newValue: String, withFeedback: Boolean) {
        Utils.writeValue(mFileName, newValue)
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(
            context
        ).edit()
        editor.putString(SETTINGS_KEY, newValue)
        editor.apply()

        if (withFeedback) {
            mVibrator!!.vibrate(VibrationEffect.createWaveform(testVibrationPattern, -1))
        }
    }

    companion object {
        private const val SETTINGS_KEY = OneplusParts.KEY_SETTINGS_PREFIX + OneplusParts.KEY_VIBSTRENGTH
        private val testVibrationPattern = longArrayOf(0, 250)

        private var mFileName: String? = null
        lateinit var DEFAULT_VALUE: String

        val isSupported: Boolean
            get() = if (mFileName != null && mFileName!!.isNotEmpty()) {
                Utils.fileWritable(mFileName)
            } else false

        fun getFile(context: Context): String? {
            mFileName = context.resources.getString(R.string.pathSystemVibStrength)
            return if (isSupported) {
                mFileName
            } else null
        }

        fun getValue(context: Context): String {
            if (!isSupported) {
                return ""
            }
            return Utils.getFileValue(mFileName, DEFAULT_VALUE)
        }

        fun restore(context: Context) {
            if (!isSupported) {
                return
            }

            var storedValue = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_KEY, DEFAULT_VALUE)

            if (storedValue == null) {
                storedValue = DEFAULT_VALUE
            }
            Utils.writeValue(mFileName, storedValue)
        }
    }
}
