/*
 * Copyright (C) 2021 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aicp.oneplus.OneplusParts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import androidx.preference.PreferenceManager
import android.util.Log

import com.aicp.oneplus.OneplusParts.audio.*
import com.aicp.oneplus.OneplusParts.preferences.VibratorStrengthPreference
import com.aicp.oneplus.OneplusParts.services.FPSInfoService

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Starting")
        context.startService(Intent(context, KeyHandler::class.java))

        // Audio gains
        EarpieceGainPreference.restore(context)
        HeadphoneGainPreference.restore(context)
        MicGainPreference.restore(context)
        SpeakerGainPreference.restore(context)

        // USB
        OneplusParts.restoreFastChargeSetting(context)
        
        // Vibrator
        VibratorStrengthPreference.restore(context)

        // FPS
        val sharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val fpsEnabled = sharedPrefs.getBoolean(OneplusParts.KEY_FPS_INFO, false)

        if (fpsEnabled) {
            context.startService(Intent(context, FPSInfoService::class.java))
        }
    }

    companion object {
        private const val TAG = "KeyHandler"
    }
}
