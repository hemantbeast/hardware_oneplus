package com.aicp.oneplus.gesture

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.UserHandle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

import com.aicp.oneplus.R

import com.android.internal.lineage.hardware.LineageHardwareManager
import com.android.internal.lineage.hardware.TouchscreenGesture

class MainSettingsFragment : PreferenceFragmentCompat() {
    private var mTouchscreenGestures: Array<TouchscreenGesture>? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.touchscreen_gesture_settings, rootKey)
        if (isTouchscreenGesturesSupported(requireContext())) {
            initTouchscreenGestures()
        }
    }

    private fun initTouchscreenGestures() {
        val manager: LineageHardwareManager = LineageHardwareManager.getInstance(context)
        mTouchscreenGestures = manager.getTouchscreenGestures()

        val actions = getDefaultGestureActions(requireContext(), mTouchscreenGestures!!)
        for (gesture in mTouchscreenGestures!!) {
            preferenceScreen.addPreference(
                TouchscreenGesturePreference(
                    requireContext(), gesture, actions[gesture.id]
                )
            )
        }
    }

    private inner class TouchscreenGesturePreference(
        context: Context,
        gesture: TouchscreenGesture,
        defaultAction: Int
    ) :
        ListPreference(context) {
        private val mContext: Context
        private val mGesture: TouchscreenGesture

        init {
            mContext = context
            mGesture = gesture
            key = buildPreferenceKey(gesture)
            setEntries(R.array.touchscreen_gesture_action_entries)
            setEntryValues(R.array.touchscreen_gesture_action_values)
            setDefaultValue(defaultAction.toString())
            summary = "%s"
            setDialogTitle(R.string.touchscreen_gesture_action_dialog_title)
            title = ResourceUtils.getLocalizedString(
                context.resources, gesture.name, TOUCHSCREEN_GESTURE_TITLE
            )
        }

        override fun callChangeListener(newValue: Any): Boolean {
            val action = newValue.toString().toInt()
            val manager: LineageHardwareManager = LineageHardwareManager.getInstance(mContext)
            return if (!manager.setTouchscreenGestureEnabled(mGesture, action > 0)) {
                false
            } else super.callChangeListener(newValue)
        }

        override fun persistString(value: String): Boolean {
            if (!super.persistString(value)) {
                return false
            }
            val action = value.toInt()
            sendUpdateBroadcast(mContext, mTouchscreenGestures!!)
            return true
        }
    }

    override fun setDivider(divider: Drawable?) {
        super.setDivider(ColorDrawable(Color.TRANSPARENT))
    }

    override fun setDividerHeight(height: Int) {
        super.setDividerHeight(0)
    }

    companion object {
        private const val KEY_TOUCHSCREEN_GESTURE = "touchscreen_gesture"
        private const val TOUCHSCREEN_GESTURE_TITLE = KEY_TOUCHSCREEN_GESTURE + "_%s_title"

        fun restoreTouchscreenGestureStates(context: Context) {
            if (!isTouchscreenGesturesSupported(context)) {
                return
            }
            val manager: LineageHardwareManager = LineageHardwareManager.getInstance(context)
            val gestures: Array<TouchscreenGesture> = manager.getTouchscreenGestures()
            val actionList = buildActionList(context, gestures)
            for (gesture in gestures) {
                manager.setTouchscreenGestureEnabled(gesture, actionList[gesture.id] > 0)
            }
            sendUpdateBroadcast(context, gestures)
        }

        private fun isTouchscreenGesturesSupported(context: Context): Boolean {
            val manager: LineageHardwareManager = LineageHardwareManager.getInstance(context)
            return manager.isSupported(LineageHardwareManager.FEATURE_TOUCHSCREEN_GESTURES)
        }

        private fun getDefaultGestureActions(
            context: Context,
            gestures: Array<TouchscreenGesture>
        ): IntArray {
            val defaultActions: IntArray = context.resources.getIntArray(
                R.array.config_defaultTouchscreenGestureActions
            )
            if (defaultActions.size >= gestures.size) {
                return defaultActions
            }
            val filledDefaultActions = IntArray(gestures.size)
            System.arraycopy(defaultActions, 0, filledDefaultActions, 0, defaultActions.size)
            return filledDefaultActions
        }

        private fun buildActionList(
            context: Context,
            gestures: Array<TouchscreenGesture>
        ): IntArray {
            val result = IntArray(gestures.size)
            val defaultActions = getDefaultGestureActions(context, gestures)
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            for (gesture in gestures) {
                val key = buildPreferenceKey(gesture)
                val defaultValue = defaultActions[gesture.id].toString()
                result[gesture.id] = prefs.getString(key, defaultValue)!!.toInt()
            }
            return result
        }

        private fun buildPreferenceKey(gesture: TouchscreenGesture): String {
            return "touchscreen_gesture_" + gesture.id
        }

        private fun sendUpdateBroadcast(
            context: Context,
            gestures: Array<TouchscreenGesture>
        ) {
            val intent = Intent(GestureConstants.UPDATE_PREFS_ACTION)
            val keycodes = IntArray(gestures.size)
            val actions = buildActionList(context, gestures)
            for (gesture in gestures) {
                keycodes[gesture.id] = gesture.keycode
            }
            intent.putExtra(GestureConstants.UPDATE_EXTRA_KEYCODE_MAPPING, keycodes)
            intent.putExtra(GestureConstants.UPDATE_EXTRA_ACTION_MAPPING, actions)
            intent.flags = Intent.FLAG_RECEIVER_REGISTERED_ONLY
            context.sendBroadcastAsUser(intent, UserHandle.CURRENT)
        }
    }
}
