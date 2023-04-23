package com.aicp.oneplus.OneplusParts.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

import com.aicp.oneplus.OneplusParts.Constants

object SPUtils {
    private fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.KEY_SHARED_PREFERENCE, Activity.MODE_PRIVATE)
    }

    fun getStringValue(context: Context, key: String, defaultVal: String): String {
        val sharedPreference = getSharedPreference(context)
        var storedValue = sharedPreference.getString(key, defaultVal)

        if (storedValue.isNullOrEmpty()) {
            storedValue = defaultVal
        }
        return storedValue
    }

    fun getBooleanValue(context: Context, key: String, defaultVal: Boolean): Boolean {
        val sharedPreference = getSharedPreference(context)
        return sharedPreference.getBoolean(key, defaultVal)
    }

    fun getIntValue(context: Context, key: String, defaultVal: Int): Int {
        val sharedPreference = getSharedPreference(context)
        return sharedPreference.getInt(key, defaultVal)
    }

    fun putStringValue(context: Context, key: String, value: String) {
        val sharePreference = getSharedPreference(context)
        val editor = sharePreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putBooleanValue(context: Context, key: String, value: Boolean) {
        val sharePreference = getSharedPreference(context)
        val editor = sharePreference.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putIntValue(context: Context, key: String, value: Int) {
        val sharePreference = getSharedPreference(context)
        val editor = sharePreference.edit()
        editor.putInt(key, value)
        editor.apply()
    }
}