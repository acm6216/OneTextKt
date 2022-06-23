package cn.guo.onetext.preference

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

open class BasePreference: PreferenceFragmentCompat() {

    private val receivers:ArrayList<BroadcastReceiver> = ArrayList()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {}

    fun String.sendBroadcast(bundle: Bundle? = null){
        val intent = Intent("${activity?.packageName}.${this}")
        if(bundle!=null) intent.putExtras(bundle)
        activity?.sendBroadcast(intent)
    }

    fun BroadcastReceiver.registerReceiver(action:String){
        receivers.add(this)
        activity?.registerReceiver(this, IntentFilter("${activity?.packageName}.${action}"))
    }
    fun BroadcastReceiver.registerReceiver(){
        activity?.unregisterReceiver(this)
        receivers.remove(this)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        receivers.forEach {
            activity?.unregisterReceiver(it)
        }
    }

    fun dp2px(dpValue: Int):Int = (dpValue*resources.displayMetrics.density+0.5f).toInt()
}