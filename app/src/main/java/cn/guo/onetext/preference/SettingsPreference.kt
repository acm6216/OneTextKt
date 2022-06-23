package cn.guo.onetext.preference

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import cn.guo.onetext.R

class SettingsPreference : BasePreference() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        findPreference<SwitchPreferenceCompat>(getString(R.string.set_dark_theme_enable_key))?.apply {
            onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    "THEME_CHANGED".sendBroadcast()
                    if (newValue == true) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    true
                }
        }
    }

}