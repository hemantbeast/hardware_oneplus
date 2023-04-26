package com.aicp.oneplus.OneplusParts.preferences

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.AttributeSet

import com.aicp.oneplus.OneplusParts.Constants
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
        DEFAULT_VALUE = mDefaultValue.toString()
        mValue = getValue(context).toInt()
        isPersistent = false

        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        mVibrator = vibratorManager.defaultVibrator
    }

    override fun changeValue(newValue: Int) {
        setValue(newValue.toString())
    }

    fun setValue(newValue: String) {
        Utils.writeValue(mFileName, newValue)
        Settings.System.putString(context.contentResolver, SETTINGS_KEY, newValue)
        mVibrator?.vibrate(VibrationEffect.createWaveform(testVibrationPattern, -1))
    }

    companion object {
        private const val SETTINGS_KEY = Constants.KEY_SETTINGS_PREFIX + Constants.KEY_VIB_STRENGTH
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
            val storedValue = Settings.System.getString(context.contentResolver, SETTINGS_KEY)
            Utils.writeValue(mFileName, storedValue)
        }
    }
}
