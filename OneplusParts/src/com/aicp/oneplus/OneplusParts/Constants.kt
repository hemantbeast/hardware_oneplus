package com.aicp.oneplus.OneplusParts

class Constants {
    companion object {
        const val KEY_SETTINGS_PREFIX = "device_setting_"
        const val KEY_SHARED_PREFERENCE = "oneplus_parts"

        // Categories
        const val KEY_CATEGORY_AUDIO = "category_audiogains"
        const val KEY_CATEGORY_FPS = "category_fps"
        const val KEY_CATEGORY_USB = "category_usb"
        const val KEY_CATEGORY_BUTTON = "category_buttons"
        const val KEY_CATEGORY_VIBRATOR = "category_vibrator"
        const val KEY_CATEGORY_BACKLIGHT = "category_backlight"

        // Audio gains
        const val KEY_HEADPHONE_GAIN = "headphone_gain"
        const val KEY_EARPIECE_GAIN = "earpiece_gain"
        const val KEY_MIC_GAIN = "mic_gain"
        const val KEY_SPEAKER_GAIN = "speaker_gain"

        // FPS
        const val KEY_FPS_INFO = "fps_info"
        const val KEY_FPS_INFO_POSITION = "fps_info_position"
        const val KEY_FPS_INFO_COLOR = "fps_info_color"
        const val KEY_FPS_INFO_TEXT_SIZE = "fps_info_text_size"

        // Misc
        const val KEY_USB2_SWITCH = "usb2_fast_charge"
        const val KEY_HWK_SWITCH = "hwk"
        const val KEY_VIB_STRENGTH = "vib_strength"
        const val KEY_BACKLIGHT = "backlight"
    }
}