package cn.guo.onetext.fragment

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import cn.guo.onetext.R
import cn.guo.onetext.preference.BasePreference

class SetSave : BasePreference() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.set_save, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
    }

    private fun setListener() {
        findPreference<Preference>(getString(R.string.set_key_save))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener { preference ->
                save(false)
            }
        }
        findPreference<Preference>(getString(R.string.set_key_save_height))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener { preference ->
                save(true)
            }
        }
    }

    private fun save(type:Boolean):Boolean{
      "SAVE_IMAGE_FILE".sendBroadcast(Bundle().apply {
          putBoolean("isBigImage", type)
      })
        return true
    }

}