package com.aicp.oneplus.gesture

import android.app.Activity
import android.app.KeyguardManager
import android.app.KeyguardManager.KeyguardDismissCallback
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler

class ScreenOffLaunchGestureActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var action = 0

        try {
            action = intent.extras!!.getInt(ACTION_KEY)
        } catch (ignored: Exception) {
        }

        val intent: Intent? = ActionUtils.getIntentByAction(this, action)
        if (intent == null) {
            finish()
        }
        Handler(mainLooper).post {
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(
                this@ScreenOffLaunchGestureActivity,
                object : KeyguardDismissCallback() {
                    override fun onDismissSucceeded() {
                        ActionUtils.startActivitySafely(this@ScreenOffLaunchGestureActivity, intent)
                        finish()
                    }

                    override fun onDismissError() {
                        finish()
                    }

                    override fun onDismissCancelled() {
                        finish()
                    }
                })
        }
    }

    companion object {
        const val ACTION_KEY = "action"
    }
}
