package cn.guo.onetext.ui.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import cn.guo.onetext.ui.app.appClassLoader
import cn.guo.onetext.ui.app.application
import cn.guo.onetext.ui.app.packageManager
import kotlin.reflect.KClass

fun <T : Activity> KClass<T>.createIntent(): Intent = Intent(application, java)

fun KClass<Intent>.createLaunchApp(packageName: String): Intent? =
    packageManager.getLaunchIntentForPackage(packageName)

fun KClass<Intent>.createSyncSettings(
    authorities: Array<out String>? = null,
    accountTypes: Array<out String>? = null
): Intent =
    Intent(Settings.ACTION_SYNC_SETTINGS).apply {
        if (!authorities.isNullOrEmpty()) {
            putExtra(Settings.EXTRA_AUTHORITIES, authorities)
        }
        if (!accountTypes.isNullOrEmpty()) {
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, accountTypes)
        }
    }

fun KClass<Intent>.createSyncSettingsWithAuthorities(vararg authorities: String) =
    createSyncSettings(authorities = authorities)

fun KClass<Intent>.createSyncSettingsWithAccountType(vararg accountTypes: String) =
    createSyncSettings(accountTypes = accountTypes)

fun KClass<Intent>.createViewAppInMarket(packageName: String): Intent =
    Uri.parse("market://details?id=$packageName").createViewIntent()

// @see com.android.documentsui.inspector.InspectorController.createGeoIntent
// @see https://developer.android.com/guide/components/intents-common.html#Maps
fun KClass<Intent>.createViewLocation(latitude: Float, longitude: Float, label: String): Intent =
    Uri.parse("geo:0,0?q=$latitude,$longitude(${Uri.encode(label)})").createViewIntent()

fun <T : Parcelable> Intent.getParcelableExtraSafe(key: String): T? {
    setExtrasClassLoader(appClassLoader)
    return getParcelableExtra(key)
}

fun Intent.getParcelableArrayExtraSafe(key: String): Array<Parcelable>? {
    setExtrasClassLoader(appClassLoader)
    return getParcelableArrayExtra(key)
}

fun <T : Parcelable?> Intent.getParcelableArrayListExtraSafe(key: String): ArrayList<T>? {
    setExtrasClassLoader(appClassLoader)
    return getParcelableArrayListExtra(key)
}

fun Intent.withChooser(title: CharSequence? = null, vararg initialIntents: Intent): Intent =
    Intent.createChooser(this, title).apply {
        putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents)
    }

fun Intent.withChooser(vararg initialIntents: Intent) = withChooser(null, *initialIntents)

fun Uri.createViewIntent(): Intent = Intent(Intent.ACTION_VIEW, this)

fun Uri.createCaptureImage(): Intent =
    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        .putExtra(MediaStore.EXTRA_OUTPUT, this)
