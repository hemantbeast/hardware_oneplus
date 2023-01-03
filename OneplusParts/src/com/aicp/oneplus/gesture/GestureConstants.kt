package com.aicp.oneplus.gesture

object GestureConstants {
    // Broadcast action for settings update
    const val UPDATE_PREFS_ACTION = "com.aicp.oneplus.gesture.UPDATE_SETTINGS"

    // Broadcast extra: keycode mapping (int[]: key = gesture ID, value = keycode)
    const val UPDATE_EXTRA_KEYCODE_MAPPING = "keycode_mappings"

    // Broadcast extra: assigned actions (int[]: key = gesture ID, value = action)
    const val UPDATE_EXTRA_ACTION_MAPPING = "action_mappings"

    // Touchscreen gesture actions
    const val ACTION_FLASHLIGHT = 1
    const val ACTION_CAMERA = 2
    const val ACTION_BROWSER = 3
    const val ACTION_DIALER = 4
    const val ACTION_EMAIL = 5
    const val ACTION_MESSAGES = 6
    const val ACTION_PLAY_PAUSE_MUSIC = 7
    const val ACTION_PREVIOUS_TRACK = 8
    const val ACTION_NEXT_TRACK = 9
    const val ACTION_VOLUME_DOWN = 10
    const val ACTION_VOLUME_UP = 11
    const val ACTION_AMBIENT_DISPLAY = 12
    const val ACTION_WAKE_DEVICE = 13
    const val ACTION_DEVICE_EXTRAS = 14
    const val ACTION_GOOGLE_MAPS = 15
    const val ACTION_GOOGLE_SEARCH = 16
}
