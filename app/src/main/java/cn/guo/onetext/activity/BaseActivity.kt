package cn.guo.onetext.activity

import android.app.Activity
import android.content.SharedPreferences
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.Intent
import android.content.Context
import android.content.ClipboardManager
import android.content.ClipData
import android.content.res.Configuration
import android.content.res.TypedArray
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import cn.guo.onetext.R
import java.io.FileOutputStream
import kotlin.reflect.KClass

open class BaseActivity:AppCompatActivity(){

    private val TAG = "BaseActivity"
    private lateinit var sharedPreferences:SharedPreferences
    private val receivers:ArrayList<BroadcastReceiver> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun BroadcastReceiver.registerReceiver(action:String){
        receivers.add(this)
        registerReceiver(this, IntentFilter("${packageName}.${action}"))
    }
    fun BroadcastReceiver.unRegisterReceiver(){
        unregisterReceiver(this)
        receivers.remove(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        receivers.forEach {
            unregisterReceiver(it)
        }
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if(sharedPreferences.getBoolean(getString(R.string.set_dark_theme_enable_key),false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        return super.onCreateView(parent, name, context, attrs)
    }

    fun String.sendBroadcast(bundle: Bundle? = null){
        val intent = Intent("${packageName}.${this}")
        if(bundle!=null) intent.putExtras(bundle)
        sendBroadcast(intent)
    }
    fun String.toast(){
        toasts(this)
    }

    fun Int.toast(){
        toasts(getString(this))
    }

    private fun toasts(str: String){
        runOnUiThread {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * activity跳转
     */
    fun activityTo(target: KClass<out Activity>){
        startActivity(Intent(this, target.java))
    }

    /**
     * dp 转 px
     */
    fun dp2px(dpValue: Int):Int = (dpValue*resources.displayMetrics.density+0.5f).toInt()

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

    fun getActionBarHeight(): Float {
        val actionbarSizeTypedArray: TypedArray = obtainStyledAttributes(
            intArrayOf(
                R.attr.actionBarSize
            )
        )
        return actionbarSizeTypedArray.getDimension(0, 0f)
    }

    fun getColorControlNormal(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorControlNormal, typedValue, true)
        return typedValue.data
    }

    open fun getSecondary(): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        return typedValue.data
    }

    /**
     * 拨打电话
     */
    fun callPhone(telNum: String) {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telNum")))
    }

    /**
     * 打开url
     */
    open fun openUrl(url: String){
        try{
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }catch (e: Exception){
            Log.d(TAG, "openUrl: open url error")
        }

    }

    /**
     * 分享文本
     */
    fun shareText(str: String){
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
            .putExtra(Intent.EXTRA_SUBJECT, "分享")
            .putExtra(Intent.EXTRA_TEXT, str).flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    /**
     * 写入粘贴板
     * @param context
     * @param message
     */
    open fun setCopyText(context: Context, message: String) {
        if (message == "") return
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("simple text", message)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * qq会话
     */
    fun qqChat(url: Long){
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=$url")
                )
            )
        }catch (e: Exception){
            Log.d(TAG, "qqChat: UnSupport")
        }
    }

    fun getVersionName():String{
        val pm = packageManager
        val pi = pm?.getPackageInfo(packageName, 0)
        return pi?.versionName?:"1.0"
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        return
    }

    /**
     * 将公共目录文件复制到app私有目录
     */
    fun copyExternalToInternal(context:Context,extUrl:Uri,internalPath:String):Boolean{
        val inputStream = context.contentResolver.openInputStream(extUrl)
        val outputStream = FileOutputStream(internalPath)
        val buffer = ByteArray(1000)
        if(inputStream == null) return false
        while (inputStream.read(buffer)>0){
            outputStream.write(buffer)
        }
        inputStream.close()
        outputStream.close()
        return true
    }
}