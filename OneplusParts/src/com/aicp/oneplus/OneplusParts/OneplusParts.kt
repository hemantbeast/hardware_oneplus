/*
 * Copyright (C) 2021 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aicp.oneplus.OneplusParts

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.TwoStatePreference

import com.aicp.oneplus.OneplusParts.audio.*
import com.aicp.oneplus.OneplusParts.backlight.DCModeSwitch
import com.aicp.oneplus.OneplusParts.R
import com.aicp.oneplus.OneplusParts.preferences.*
import com.aicp.oneplus.OneplusParts.services.FPSInfoService
import com.aicp.oneplus.OneplusParts.utils.SPUtils
import com.aicp.oneplus.OneplusParts.utils.Utils

class OneplusParts : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    // Audio gains
    private var mEarGain: EarpieceGainPreference? = null
    private var mHeadphoneGain: HeadphoneGainPreference? = null
    private var mMicGain: MicGainPreference? = null
    private var mSpeakerGain: SpeakerGainPreference? = null

    // FPS Info
    private var mFpsInfoPosition: ListPreference? = null
    private var mFpsInfoColor: ListPreference? = null
    private var mFpsInfo: SwitchPreference? = null
    private var mFpsInfoTextSizePreference: CustomSeekBarPreference? = null

    // Backlight dimmer
    private var mBacklightSwitch: TwoStatePreference? = null

    // USB fast charge
    private var mUSB2FastChargeModeSwitch: SwitchPreference? = null

    // Hardware key switch
    private var mHWKSwitchPreference: TwoStatePreference? = null

    // Vibrator
    private var mVibratorStrength: VibratorStrengthPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.main)
        requireActivity().actionBar!!.setDisplayHomeAsUpEnabled(true)

        setAudioGainPreference()
        setFPSInfoPreference(requireContext())
        setUsbFastCharge(requireContext())
        setHWKPreference()
        setVibratorPreference()
        setBacklightDimmer()
    }

    override fun onResume() {
        super.onResume()

        if (isFeatureSupported(requireContext(), R.string.pathfpsInfo)) {
            mFpsInfo?.isChecked = isFPSOverlayRunning()
        }
    }

    // Audio gain initialization
    private fun setAudioGainPreference() {
        val audioGainsCategory = findPreference<PreferenceCategory>(Constants.KEY_CATEGORY_AUDIO)
        var audioGainsRemoved = 0

        mEarGain = findPreference(Constants.KEY_EARPIECE_GAIN)
        if (mEarGain != null) {
            if (mEarGain!!.supported()) {
                mEarGain?.isEnabled = true
            } else {
                mEarGain?.parent?.removePreference(mEarGain!!)
                audioGainsRemoved += 1
            }
        }

        mHeadphoneGain = findPreference(Constants.KEY_HEADPHONE_GAIN)
        if (mHeadphoneGain != null) {
            if (mHeadphoneGain!!.supported()) {
                mHeadphoneGain?.isEnabled = true
            } else {
                mHeadphoneGain?.parent?.removePreference(mHeadphoneGain!!)
                audioGainsRemoved += 1
            }
        }

        mMicGain = findPreference(Constants.KEY_MIC_GAIN)
        if (mMicGain != null) {
            if (mMicGain!!.supported()) {
                mMicGain?.isEnabled = true
            }else {
                mMicGain?.parent?.removePreference(mMicGain!!)
                audioGainsRemoved += 1
            }
        }

        mSpeakerGain = findPreference(Constants.KEY_SPEAKER_GAIN)
        if (mSpeakerGain != null) {
            if (mSpeakerGain!!.supported()) {
                mSpeakerGain?.isEnabled = true
            } else {
                mSpeakerGain?.parent?.removePreference(mSpeakerGain!!)
                audioGainsRemoved += 1
            }
        }

        if (audioGainsRemoved == 4) audioGainsCategory?.parent?.removePreference(audioGainsCategory)
    }

    // FPS overlay initialization
    private fun setFPSInfoPreference(context: Context) {
        val fpsCategory = findPreference<PreferenceCategory>(Constants.KEY_CATEGORY_FPS)
        if (!isFeatureSupported(context, R.string.pathfpsInfo)) {
            fpsCategory?.parent?.removePreference(fpsCategory)
            return
        }

        mFpsInfo = findPreference(Constants.KEY_FPS_INFO)
        mFpsInfo?.isChecked = SPUtils.getBooleanValue(context,
            Constants.KEY_SETTINGS_PREFIX + Constants.KEY_FPS_INFO, false)
        mFpsInfo?.onPreferenceChangeListener = this

        mFpsInfoPosition = findPreference(Constants.KEY_FPS_INFO_POSITION)
        mFpsInfoPosition?.onPreferenceChangeListener = this

        mFpsInfoColor = findPreference(Constants.KEY_FPS_INFO_COLOR)
        mFpsInfoColor?.onPreferenceChangeListener = this

        mFpsInfoTextSizePreference = findPreference(Constants.KEY_FPS_INFO_TEXT_SIZE)
        mFpsInfoTextSizePreference?.onPreferenceChangeListener = this
    }

    // USB fast charge initialization
    private fun setUsbFastCharge(context: Context) {
        val usbCategory = findPreference<PreferenceCategory>(Constants.KEY_CATEGORY_USB)
        if (!isFeatureSupported(context, R.string.pathUsbFastCharge)) {
            usbCategory?.parent?.removePreference(usbCategory)
            return
        }

        mUSB2FastChargeModeSwitch = findPreference(Constants.KEY_USB2_SWITCH)
        val isFileWritable = Utils.fileWritable(getString(R.string.pathUsbFastCharge))

        if (isFileWritable) {
            mUSB2FastChargeModeSwitch?.isEnabled = true
            mUSB2FastChargeModeSwitch?.isChecked = SPUtils.getBooleanValue(context,
                Constants.KEY_SETTINGS_PREFIX + Constants.KEY_USB2_SWITCH,
                Utils.getFileValueAsBoolean(getString(R.string.pathUsbFastCharge), false))
            mUSB2FastChargeModeSwitch?.onPreferenceChangeListener = this
        } else {
            mUSB2FastChargeModeSwitch?.isEnabled = false
        }
    }

    // HWK button initialization
    private fun setHWKPreference() {
        val buttonsCategory = findPreference<PreferenceCategory>(Constants.KEY_CATEGORY_BUTTON)
        mHWKSwitchPreference = findPreference(Constants.KEY_HWK_SWITCH)

        if (mHWKSwitchPreference != null && HWKSwitch.isSupported) {
            mHWKSwitchPreference!!.isEnabled = true
            mHWKSwitchPreference!!.isChecked = HWKSwitch.isCurrentlyEnabled(requireContext())
            mHWKSwitchPreference!!.onPreferenceChangeListener = HWKSwitch(requireContext())
        } else {
            buttonsCategory?.parent?.removePreference(buttonsCategory)
        }
    }

    // Vibrator strength initialization
    private fun setVibratorPreference() {
        val vibratorCategory = findPreference<PreferenceCategory>(Constants.KEY_CATEGORY_VIBRATOR)
        mVibratorStrength = findPreference(Constants.KEY_VIB_STRENGTH)

        if (mVibratorStrength != null && VibratorStrengthPreference.isSupported) {
            mVibratorStrength!!.isEnabled = true
        } else {
            vibratorCategory?.parent?.removePreference(vibratorCategory)
        }
    }

    // Backlight dimmer initialization
    private fun setBacklightDimmer() {
        val backlightCategory = findPreference<PreferenceCategory>(Constants.KEY_CATEGORY_BACKLIGHT)
        mBacklightSwitch = findPreference(Constants.KEY_BACKLIGHT)

        if (mBacklightSwitch != null && DCModeSwitch.isSupported) {
            mBacklightSwitch!!.isEnabled = true
            mBacklightSwitch!!.isChecked = DCModeSwitch.isCurrentlyEnabled(requireContext())
            mBacklightSwitch!!.onPreferenceChangeListener = DCModeSwitch(requireContext())
        } else {
            backlightCategory?.parent?.removePreference(backlightCategory)
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        if (preference === mFpsInfo) {
            val enabled = newValue as Boolean
            val fpsInfo = Intent(
                requireContext(),
                FPSInfoService::class.java
            )
            if (enabled) {
                requireContext().startService(fpsInfo)
            } else {
                requireContext().stopService(fpsInfo)
            }
            return true
        } else if (preference === mFpsInfoPosition) {
            val position = newValue.toString().toInt()
            if (FPSInfoService.isPositionChanged(requireContext(), position)) {
                FPSInfoService.setPosition(requireContext(), position)
                if (isFPSOverlayRunning()) {
                    restartFpsInfo(requireContext())
                }
            }
            return true
        } else if (preference === mFpsInfoColor) {
            val color = newValue.toString().toInt()
            if (FPSInfoService.isColorChanged(requireContext(), color)) {
                FPSInfoService.setColorIndex(requireContext(), color)
                if (isFPSOverlayRunning()) {
                    restartFpsInfo(requireContext())
                }
            }
            return true
        } else if (preference === mFpsInfoTextSizePreference) {
            val size = newValue.toString().toInt()
            if (FPSInfoService.isSizeChanged(requireContext(), size - 1)) {
                FPSInfoService.setSizeIndex(requireContext(), size - 1)
                if (isFPSOverlayRunning()) {
                    restartFpsInfo(requireContext())
                }
            }
            return true
        } else if (preference == mUSB2FastChargeModeSwitch) {
            val enabled = newValue as Boolean
            Utils.writeValue(getString(R.string.pathUsbFastCharge), if (enabled) "1" else "0")
            SPUtils.putBooleanValue(requireContext(),
                Constants.KEY_SETTINGS_PREFIX + Constants.KEY_USB2_SWITCH, enabled)
            return true
        }
        return false
    }

    @Suppress("DEPRECATION")
    private fun isFPSOverlayRunning(): Boolean {
        val am = requireContext().getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager

        for (service in am.getRunningServices(Int.MAX_VALUE)){
            if (FPSInfoService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun restartFpsInfo(context: Context) {
        val fpsInfo = Intent(context, FPSInfoService::class.java)
        context.stopService(fpsInfo)
        context.startService(fpsInfo)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                requireActivity().finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun isFeatureSupported(ctx: Context, feature: Int): Boolean {
            return try {
                ctx.resources.getString(feature).isNotEmpty()
            }
            catch (e: Exception) {
                false
            }
        }

        fun restoreFastChargeSetting(context: Context) {
            if (Utils.fileWritable(context.getString(R.string.pathUsbFastCharge))) {
                val value = SPUtils.getBooleanValue(context,
                    Constants.KEY_SETTINGS_PREFIX + Constants.KEY_USB2_SWITCH,
                    Utils.getFileValueAsBoolean(context.getString(R.string.pathUsbFastCharge), false)
                )
                Utils.writeValue(context.getString(R.string.pathUsbFastCharge), if (value) "1" else "0")
            }
        }
    }
}
