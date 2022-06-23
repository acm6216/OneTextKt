package cn.guo.onetext.alerter.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.DimenRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import cn.guo.onetext.R
import cn.guo.onetext.alerter.Alert

fun Alert.getDimenPixelSize(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

@RequiresApi(Build.VERSION_CODES.P)
fun Alert.notchHeight() = (context as? Activity)?.window?.decorView?.rootWindowInsets?.displayCutout?.safeInsetTop
    ?: 0

fun Context.getRippleDrawable(): Drawable? {
    val typedValue = TypedValue()
    theme.resolveAttribute(R.attr.selectableItemBackground, typedValue, true)
    val imageResId = typedValue.resourceId
    return ContextCompat.getDrawable(this, imageResId)
}