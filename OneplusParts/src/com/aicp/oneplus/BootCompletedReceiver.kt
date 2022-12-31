/*
 * Copyright (C) 2021 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aicp.oneplus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import androidx.preference.PreferenceManager
import android.util.Log

import com.aicp.oneplus.audio.*

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Starting")
        context.startService(Intent(context, KeyHandler::class.java))

        maybeImportOldSettings(context)
        restoreAfterUserSwitch(context)
    }

    private fun maybeImportOldSettings(context: Context) {
        val resolver = context.contentResolver
        val imported = Settings.System.getInt(resolver, "omni_device_setting_imported", 0) != 0

        if (!imported) {
            val sharedPrefs: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)

            val audioEarpieceGain = sharedPrefs.getString(
                OneplusParts.KEY_EARPIECE_GAIN,
                EarpieceGainPreference.getDefaultValue(context)
            )

            Settings.System.putString(
                resolver,
                EarpieceGainPreference.SETTINGS_KEY,
                audioEarpieceGain
            )

            restore(EarpieceGainPreference.getFile(context), audioEarpieceGain!!)
            val audioMicGain = sharedPrefs.getString(
                OneplusParts.KEY_MIC_GAIN,
                MicGainPreference.getDefaultValue(context)
            )

            Settings.System.putString(resolver, MicGainPreference.SETTINGS_KEY, audioMicGain)
            restore(MicGainPreference.getFile(context), audioMicGain!!)

            val audioSpeakerGain = sharedPrefs.getString(
                OneplusParts.KEY_SPEAKER_GAIN,
                SpeakerGainPreference.getDefaultValue(context)
            )

            Settings.System.putString(
                resolver,
                SpeakerGainPreference.SETTINGS_KEY,
                audioSpeakerGain
            )
            restore(SpeakerGainPreference.getFile(context), audioSpeakerGain!!)
            val audioHeadphoneGain = sharedPrefs.getString(
                OneplusParts.KEY_HEADPHONE_GAIN,
                HeadphoneGainPreference.getDefaultValue(context)
            )

            Settings.System.putString(
                resolver,
                HeadphoneGainPreference.SETTINGS_KEY,
                audioHeadphoneGain
            )
            restoreDual(HeadphoneGainPreference.getFile(context), audioHeadphoneGain!!)
            Settings.System.putInt(resolver, "omni_device_setting_imported", 1)
        }
    }

    private fun restoreAfterUserSwitch(context: Context) {
        val resolver = context.contentResolver

        restore(
            EarpieceGainPreference.getFile(context), Settings.System.getString(
                resolver,
                EarpieceGainPreference.SETTINGS_KEY
            )
        )
        restore(
            MicGainPreference.getFile(context), Settings.System.getString(
                resolver,
                MicGainPreference.SETTINGS_KEY
            )
        )
        restoreDual(
            HeadphoneGainPreference.getFile(context), Settings.System.getString(
                resolver,
                HeadphoneGainPreference.SETTINGS_KEY
            )
        )
        restore(
            SpeakerGainPreference.getFile(context), Settings.System.getString(
                resolver,
                SpeakerGainPreference.SETTINGS_KEY
            )
        )
    }

    private fun restore(file: String?, enabled: Boolean) {
        if (file == null) {
            return
        }
        Utils.writeValue(file, if (enabled) "1" else "0")
    }

    private fun restore(file: String?, value: String) {
        if (file == null) {
            return
        }
        Utils.writeValue(file, value)
    }

    private fun restoreDual(file: String?, value: String) {
        if (file == null) {
            return
        }
        Utils.writeValueDual(file, value)
    }

    companion object {
        private const val TAG = "KeyHandler"
    }
}
