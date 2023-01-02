/*
 * Copyright (C) 2021 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aicp.oneplus

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.*

import com.aicp.oneplus.audio.*;
import com.aicp.oneplus.R;
import com.aicp.oneplus.preferences.CustomSeekBarPreference
import com.aicp.oneplus.services.FPSInfoService

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

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.main)
        requireActivity().actionBar!!.setDisplayHomeAsUpEnabled(true)

        setAudioGainPreference()
        setFPSInfoPreference(requireContext())
    }

    override fun onResume() {
        super.onResume()

        if (isFeatureSupported(requireContext(), R.string.pathfpsInfo)) {
            mFpsInfo?.isChecked = isFPSOverlayRunning()
        }
    }

    private fun setAudioGainPreference() {
        val audioGainsCategory = findPreference<PreferenceCategory>(KEY_CATEGORY_AUDIO)
        var audioGainsRemoved = 0

        mEarGain = findPreference(KEY_EARPIECE_GAIN)
        if (mEarGain != null) {
            if (mEarGain!!.supported()) {
                mEarGain?.isEnabled = mEarGain!!.supported()
            } else {
                mEarGain?.parent?.removePreference(mEarGain!!)
                audioGainsRemoved += 1
            }
        }

        mHeadphoneGain = findPreference(KEY_HEADPHONE_GAIN);
        if (mHeadphoneGain != null) {
            if (mHeadphoneGain!!.supported()) {
                mHeadphoneGain?.isEnabled = true
            } else {
                mHeadphoneGain?.parent?.removePreference(mHeadphoneGain!!)
                audioGainsRemoved += 1
            }
        }

        mMicGain = findPreference(KEY_MIC_GAIN)
        if (mMicGain != null) {
            if (mMicGain!!.supported()) {
                mMicGain?.isEnabled = true
            }else {
                mMicGain?.parent?.removePreference(mMicGain!!)
                audioGainsRemoved += 1
            }
        }

        mSpeakerGain = findPreference(KEY_SPEAKER_GAIN)
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

    private fun setFPSInfoPreference(context: Context) {
        val prefs = requireActivity().getSharedPreferences(
            "main",
            Activity.MODE_PRIVATE
        )
        val fpsCategory = findPreference<PreferenceCategory>(KEY_CATEGORY_FPS)

        if (isFeatureSupported(context, R.string.pathfpsInfo)) {
            mFpsInfo = findPreference(KEY_FPS_INFO)
            mFpsInfo?.isChecked = prefs.getBoolean(KEY_FPS_INFO, false)
            mFpsInfo?.setOnPreferenceChangeListener(this)

            mFpsInfoPosition = findPreference(KEY_FPS_INFO_POSITION)
            mFpsInfoPosition?.setOnPreferenceChangeListener(this)

            mFpsInfoColor = findPreference(KEY_FPS_INFO_COLOR)
            mFpsInfoColor?.setOnPreferenceChangeListener(this)

            mFpsInfoTextSizePreference = findPreference(KEY_FPS_INFO_TEXT_SIZE);
            mFpsInfoTextSizePreference?.setOnPreferenceChangeListener(this)
        } else {
            fpsCategory?.parent?.removePreference(fpsCategory)
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
        }
        return false
    }

    private fun isFPSOverlayRunning(): Boolean {
        val am = requireContext().getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager
        for (service in am.getRunningServices(Int.MAX_VALUE)) if (FPSInfoService::class.java.name == service.service.className) return true
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
        private const val TAG = "OneplusParts"

        public const val KEY_SETTINGS_PREFIX = "device_setting_";

        private const val KEY_CATEGORY_AUDIO = "category_audiogains";
        private const val KEY_CATEGORY_FPS = "category_fps"

        public const val KEY_HEADPHONE_GAIN = "headphone_gain";
        public const val KEY_EARPIECE_GAIN = "earpiece_gain";
        public const val KEY_MIC_GAIN = "mic_gain";
        public const val KEY_SPEAKER_GAIN = "speaker_gain";

        const val KEY_FPS_INFO = "fps_info"
        const val KEY_FPS_INFO_POSITION = "fps_info_position"
        const val KEY_FPS_INFO_COLOR = "fps_info_color"
        const val KEY_FPS_INFO_TEXT_SIZE = "fps_info_text_size"

        fun isFeatureSupported(ctx: Context, feature: Int): Boolean {
            return try {
                ctx.resources.getString(feature).isNotEmpty()
            }
            catch (e: Exception) {
                false
            }
        }
    }
}
