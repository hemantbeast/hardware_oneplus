/*
 * Copyright (C) 2021 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aicp.oneplus.OneplusParts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log

import com.aicp.oneplus.OneplusParts.audio.*
import com.aicp.oneplus.OneplusParts.backlight.DCModeSwitch
import com.aicp.oneplus.OneplusParts.preferences.HWKSwitch
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

        // Backlight dimmer
        DCModeSwitch.restore(context)

        // HWK
        HWKSwitch.restore(context)

        // USB
        OneplusParts.restoreFastChargeSetting(context)
        
        // Vibrator
        VibratorStrengthPreference.restore(context)

        // FPS
        val fpsEnabled = Settings.System.getInt(context.contentResolver,
            Constants.KEY_SETTINGS_PREFIX + Constants.KEY_FPS_INFO, 0)

        if (fpsEnabled == 1) {
            context.startService(Intent(context, FPSInfoService::class.java))
        }
    }

    companion object {
        private const val TAG = "KeyHandler"
    }
}
