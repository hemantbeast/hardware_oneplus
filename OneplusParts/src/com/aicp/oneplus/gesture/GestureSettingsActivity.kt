package com.aicp.oneplus.gesture

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity
import com.android.settingslib.widget.R

class GestureSettingsActivity : CollapsingToolbarBaseActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            getNewFragment()?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, it)
                    .commit()
            };
        }
    }

    private fun getNewFragment(): PreferenceFragmentCompat? {
        return MainSettingsFragment()
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        val instantiate: Fragment = supportFragmentManager.fragmentFactory.instantiate(
            ClassLoader.getSystemClassLoader(), pref.fragment!!
        )
        supportFragmentManager.beginTransaction().replace(
            R.id.content_frame, instantiate
        ).addToBackStack(pref.key).commit()

        return true
    }
}
