package cn.guo.onetext

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class MainApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.set_dark_theme_enable_key),false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

}