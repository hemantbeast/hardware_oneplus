package com.aicp.oneplus.gesture

import android.app.KeyguardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.UserHandle

class ActionUtils {
    companion object {
        private fun getLaunchableIntent(context: Context, intent: Intent): Intent? {
            val pm: PackageManager = context.packageManager
            val resInfo = pm.queryIntentActivities(intent, 0)
            return if (resInfo.isEmpty()) {
                null
            } else pm.getLaunchIntentForPackage(resInfo[0].activityInfo.packageName)
        }

        private fun getBrowserIntent(context: Context): Intent? {
            return getLaunchableIntent(
                context,
                Intent(Intent.ACTION_VIEW, Uri.parse("http:"))
            )
        }

        private fun getDialerIntent(): Intent? {
            return Intent(Intent.ACTION_DIAL, null)
        }

        private fun getEmailIntent(context: Context): Intent? {
            return getLaunchableIntent(
                context,
                Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"))
            )
        }

        private fun getMessagesIntent(context: Context): Intent? {
            return getLaunchableIntent(
                context,
                Intent(Intent.ACTION_VIEW, Uri.parse("sms:"))
            )
        }

        private fun getDeviceExtrasIntent(context: Context): Intent? {
            val intent = Intent()
            val cn = ComponentName(
                "com.aicp.oneplus",
                "com.aicp.oneplus.OneplusPartsActivity"
            )
            intent.component = cn
            return intent
        }

        private fun getGoogleMapsIntent(context: Context): Intent? {
            val intent = Intent()
            val cn = ComponentName(
                "com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity"
            )
            intent.component = cn
            return intent
        }

        private fun getGoogleSearchIntent(context: Context): Intent? {
            val intent = Intent()
            val cn = ComponentName(
                "com.google.android.googlequicksearchbox",
                "com.google.android.googlequicksearchbox.SearchActivity"
            )
            intent.component = cn
            return intent
        }

        fun getIntentByAction(context: Context?, action: Int): Intent? {
            var intent: Intent? = null
            when (action) {
                GestureConstants.ACTION_BROWSER -> {
                    intent = context?.let { getBrowserIntent(it) }
                }
                GestureConstants.ACTION_DIALER -> {
                    intent = getDialerIntent()
                }
                GestureConstants.ACTION_EMAIL -> {
                    intent = context?.let { getEmailIntent(it) }
                }
                GestureConstants.ACTION_MESSAGES -> {
                    intent = context?.let { getMessagesIntent(it) }
                }
                GestureConstants.ACTION_DEVICE_EXTRAS -> {
                    intent = context?.let { getDeviceExtrasIntent(it) }
                }
                GestureConstants.ACTION_GOOGLE_MAPS -> {
                    intent = context?.let { getGoogleMapsIntent(it) }
                }
                GestureConstants.ACTION_GOOGLE_SEARCH -> {
                    intent = context?.let { getGoogleSearchIntent(it) }
                }
            }
            return intent
        }

        @JvmStatic
        fun triggerAction(context: Context, action: Int) {
            var intent = getIntentByAction(context, action) ?: return
            val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (km.isKeyguardLocked) {
                intent = Intent()
                intent.setClassName(
                    "com.aicp.oneplus",
                    "com.aicp.oneplus.gesture.ScreenOffLaunchGestureActivity"
                )
                intent.putExtra(ScreenOffLaunchGestureActivity.ACTION_KEY, action)
            }
            startActivitySafely(context, intent)
        }

        fun startActivitySafely(context: Context, intent: Intent?) {
            if (intent == null) {
                return
            }
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            try {
                val user = UserHandle(UserHandle.USER_CURRENT)
                context.startActivityAsUser(intent, null, user)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
