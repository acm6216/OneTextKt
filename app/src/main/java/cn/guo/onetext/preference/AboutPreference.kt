package cn.guo.onetext.preference

import android.graphics.Matrix
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.BitmapShader
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import cn.guo.onetext.R
import cn.guo.onetext.Utils
import cn.guo.onetext.activity.BaseActivity

class AboutPreference : BasePreference(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.about, rootKey)
        init()
    }

    private fun init(){
        findPreference<Preference>(getString(R.string.about_key_version))?.apply {
            summary = (activity as BaseActivity).getVersionName()
            isEnabled=true
        }
        findPreference<Preference>(getString(R.string.about_key_info))?.apply {
            val bitmap = getBitmap(R.mipmap.ic_launcher)
            icon=BitmapDrawable(context.resources, bitmap?.let { transform(it) })
        }
        findPreference<Preference>(getString(R.string.about_key_developer_yjr))?.apply {
            var bitmap = getBitmap(R.drawable.ic_yjr)
            bitmap = scaleBitmap(bitmap,dp2px(24),dp2px(24))
            icon=BitmapDrawable(context.resources, bitmap?.let { transform(it) })
        }
        findPreference<Preference>(getString(R.string.about_key_version))?.apply {
            setOnPreferenceClickListener {
                AlertDialog.Builder(context).apply {
                    setTitle("更新日志：")
                    setMessage(Utils.readAssetsTxt(context,"version.build"))
                    setPositiveButton("关闭",null)
                    show()
                }
                true
            }
        }
    }

    private fun scaleBitmap(origin: Bitmap?, newWidth: Int, newHeight: Int): Bitmap? {
        if (origin == null) {
            return null
        }
        val height = origin.height
        val width = origin.width
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight) // 使用后乘
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (!origin.isRecycled) {
            origin.recycle()
        }
        return newBM
    }

    private fun getBitmap(vectorDrawableId: Int): Bitmap? {
        var bitmap: Bitmap? = null
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val vectorDrawable: Drawable = context?.getDrawable(vectorDrawableId)!!
            bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
        } else {
            bitmap = BitmapFactory.decodeResource(context?.resources, vectorDrawableId)
        }
        return bitmap
    }

    private fun transform(source: Bitmap): Bitmap? {
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }
        val bitmap = Bitmap.createBitmap(size, size, source.config)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        squaredBitmap.recycle()
        return bitmap
    }

}