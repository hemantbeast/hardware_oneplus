/*
 * Copyright (C) 2021 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aicp.oneplus

import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat

import com.aicp.oneplus.audio.*;
import com.aicp.oneplus.R;

class OneplusParts : PreferenceFragmentCompat() {
    private var mEarGain: EarpieceGainPreference? = null
    private var mHeadphoneGain: HeadphoneGainPreference? = null
    private var mMicGain: MicGainPreference? = null
    private var mSpeakerGain: SpeakerGainPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.main)
        requireActivity().actionBar!!.setDisplayHomeAsUpEnabled(true)

        val audioGainsCategory = findPreference<PreferenceCategory>(KEY_AUDIOGAINS_CATEGORY)
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

    override fun addPreferencesFromResource(preferencesResId: Int) {
        super.addPreferencesFromResource(preferencesResId)
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

        private const val KEY_AUDIOGAINS_CATEGORY = "category_audiogains";

        public const val KEY_HEADPHONE_GAIN = "headphone_gain";
        public const val KEY_EARPIECE_GAIN = "earpiece_gain";
        public const val KEY_MIC_GAIN = "mic_gain";
        public const val KEY_SPEAKER_GAIN = "speaker_gain";
    }
}
