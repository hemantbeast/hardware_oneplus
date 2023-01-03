package com.aicp.oneplus.gesture

import android.content.res.Resources
import android.util.Log
import java.util.*

class ResourceUtils {
    companion object {
        private val TAG = ResourceUtils::class.java.simpleName

        fun getLocalizedString(
            res: Resources,
            stringName: String,
            stringFormat: String?
        ): String? {
            val name = stringName.lowercase(Locale.getDefault()).replace(" ", "_")
            val nameRes = String.format(stringFormat!!, name)
            return getStringForResourceName(res, nameRes, stringName)
        }

        fun getStringForResourceName(
            res: Resources,
            resourceName: String,
            defaultValue: String?
        ): String? {
            val resId: Int =
                res.getIdentifier(resourceName, "string", "com.aicp.oneplus")
            return if (resId <= 0) {
                Log.e(TAG, "No resource found for $resourceName")
                defaultValue
            } else {
                res.getString(resId)
            }
        }
    }
}
