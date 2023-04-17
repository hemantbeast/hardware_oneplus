package com.aicp.oneplus.OneplusParts.preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder

import com.aicp.oneplus.OneplusParts.OneplusParts
import com.aicp.oneplus.OneplusParts.R
import com.aicp.oneplus.OneplusParts.Utils


class VibratorStrengthPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs), SeekBar.OnSeekBarChangeListener {
    private var mSeekBar: SeekBar? = null
    private var mOldStrength = 0
    private var mMinValue = 0
    private var mMaxValue = 0
    private var mVibrator: Vibrator? = null

    init {
        mMinValue = 116
        mMaxValue = 1800

        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        mVibrator = vibratorManager.defaultVibrator
        layoutResource = R.layout.preference_seek_bar
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        mOldStrength = getValue(context).toInt()
        mSeekBar = holder.findViewById(R.id.seekbar) as SeekBar
        mSeekBar!!.max = mMaxValue - mMinValue
        mSeekBar!!.progress = mOldStrength - mMinValue
        mSeekBar!!.setOnSeekBarChangeListener(this)
    }

    fun setValue(newValue: String, withFeedback: Boolean) {
        Utils.writeValue(FILE_LEVEL, newValue)
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(
            context
        ).edit()
        editor.putString(SETTINGS_KEY, newValue)
        editor.apply()

        if (withFeedback) {
            mVibrator!!.vibrate(VibrationEffect.createWaveform(testVibrationPattern, -1))
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // NA
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // NA
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        setValue("${seekBar?.progress?.plus(mMinValue)}", true)
    }

    companion object {
        private const val SETTINGS_KEY = OneplusParts.KEY_SETTINGS_PREFIX + OneplusParts.KEY_VIBSTRENGTH
        
        private const val FILE_LEVEL = "/sys/class/leds/vibrator/vmax_mv"
        private val testVibrationPattern = longArrayOf(0, 250)

        fun isSupported(): Boolean {
            return Utils.fileWritable(FILE_LEVEL)
        }

        fun getValue(context: Context): String {
            return Utils.getFileValue(FILE_LEVEL, "1800")
        }

        fun restore(context: Context) {
            if (!isSupported()) {
                return
            }

            val storedValue = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_KEY, "1800")
            Utils.writeValue(FILE_LEVEL, storedValue!!)
        }
    }
}
