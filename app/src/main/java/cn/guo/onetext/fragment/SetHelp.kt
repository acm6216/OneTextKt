package cn.guo.onetext.fragment

import android.os.Bundle
import cn.guo.onetext.R
import cn.guo.onetext.preference.BasePreference

class SetHelp : BasePreference() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.set_help, rootKey)
    }
}