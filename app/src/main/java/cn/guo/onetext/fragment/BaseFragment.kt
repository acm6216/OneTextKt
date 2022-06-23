package cn.guo.onetext.fragment

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cn.guo.onetext.R
import cn.guo.onetext.alerter.Alerter

open class BaseFragment : Fragment() {

    private val receivers:ArrayList<BroadcastReceiver> = ArrayList()

    fun BroadcastReceiver.registerReceiver(action:String){
        receivers.add(this)
        activity?.registerReceiver(this, IntentFilter("${activity?.packageName}.${action}"))
    }
    fun BroadcastReceiver.unregisterReceiver(){
        activity?.unregisterReceiver(this)
        receivers.remove(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        receivers.forEach {
            activity?.unregisterReceiver(it)
        }
    }

    /**
     * 获取屏幕尺寸
     */
    fun getWindowSize(isWidth: Boolean): Int {
        val displayMetrics = resources.displayMetrics
        //  DisplayMetrics：显示度量
        val widthPixels = displayMetrics.widthPixels //当前屏幕宽
        val heightPixels = displayMetrics.heightPixels //当前屏幕高
        return if (isWidth) widthPixels else heightPixels
    }

    fun String.showAlerter(){
        activity?.let {
            Alerter.create(it)
                .setBackgroundColorInt(ContextCompat.getColor(context!!,R.color.colorBlue))
                .setText(this)
                .setDuration(2000)
                .show()
        }
    }
    fun String.sendBroadcast(bundle: Bundle? = null){
        val intent = Intent("${activity?.packageName}.${this}")
        if(bundle!=null) intent.putExtras(bundle)
        activity?.sendBroadcast(intent)
    }
}