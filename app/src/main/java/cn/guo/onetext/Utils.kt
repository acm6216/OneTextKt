package cn.guo.onetext

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils
import android.view.PixelCopy
import android.view.View
import org.jetbrains.annotations.NotNull
import java.io.*
import java.util.*
import kotlin.properties.Delegates

object Utils {

    private const val TAG = "Utils"

    private val random = Random()
    private val darkColorList = intArrayOf(
        -0xa54094,
        -0x86fa0,
        -0x9c3f43,
        -0x127c81,
        -0xa46b2,
        -0x356b7d,
        -0xce592d,
        -0x7438c3,
        -0x78593a,
        -0x208667,
        -0x2957a8,
        -0x66803d,
        -0x224685,
        -0x2c211b
    )
    private val colorList = intArrayOf(
        -0x752d69,
        -0x6577d,
        -0x773034,
        -0xe6367,
        -0x83a95,
        -0x2d5a6a,
        -0x984222,
        -0x6330a6,
        -0x654b31,
        -0x1a6c53,
        -0x1d3c76,
        -0x4d602e,
        -0x1d3b70,
        -0x1d3b70
    )

    fun getDarkRandomColor(): Int {
        return darkColorList[random.nextInt(
            20
        ) % colorList.size]
    }

    //读取assets下文本
    fun readAssetsTxt(context: Context, filename: String?): String? {
        var result = ""
        try {
            val inputStream = context.assets.open(filename!!)
            var inputStreamReader: InputStreamReader? = null
            try {
                inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
            val reader = BufferedReader(inputStreamReader)
            val sb = StringBuffer("")
            var line: String?
            try {
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                    sb.append("\n")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            result = sb.toString()
        } catch (e: Exception) {
        }
        return result
    }

    /**
     * 获取文件mimeType
     * @param filePath 文件路径
     */
    fun getMimeType(filePath: String): String? {
        val mmr = MediaMetadataRetriever()
        var mime: String? = "text/plain"
        if (filePath != null) {
            mime = try {
                mmr.setDataSource(filePath)
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
            } catch (e: IllegalStateException) {
                return mime
            } catch (e: IllegalArgumentException) {
                return mime
            } catch (e: RuntimeException) {
                return mime
            }
        }
        return mime
    }

    fun getFilePathFromURI(context: Context, contentUri: Uri?): String? {
        val rootDataDir: File? = context.getExternalFilesDir(null)
        val fileName: String = getFileName(contentUri)!!
        if (!TextUtils.isEmpty(fileName)) {
            val copyFile = File(rootDataDir, File.separator.toString() + fileName)
            copyFile(context, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }
    private fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }
    private fun copyFile(context: Context, srcUri: Uri?, dstFile: File?) {
        try {
            val inputStream = context.contentResolver.openInputStream(srcUri!!) ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile)
            copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    @Throws(java.lang.Exception::class, IOException::class)
    fun copyStream(input: InputStream?, output: OutputStream?): Int {
        val BUFFER_SIZE = 1024 * 2
        val buffer = ByteArray(BUFFER_SIZE)
        val bis = BufferedInputStream(input, BUFFER_SIZE)
        val bos = BufferedOutputStream(output, BUFFER_SIZE)
        var count = 0
        var n = 0
        try {
            while (bis.read(buffer, 0, BUFFER_SIZE).also { n = it } != -1) {
                bos.write(buffer, 0, n)
                count += n
            }
            bos.flush()
        } finally {
            try {
                bos.close()
            } catch (e: IOException) {
            }
            try {
                bis.close()
            } catch (e: IOException) {
            }
        }
        return count
    }

    /**
     * 保存图片到pictures目录,并刷新
     * Android Q适配
     *
     * @param context
     * @param bitmap
     * @param fileName
     */
    fun saveBitmapToPicture(context: Context, bitmap: Bitmap?, fileName: String?): Boolean {
        if(bitmap==null) return false
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.DESCRIPTION, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")

        val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values)
        var outputStream: OutputStream? = null
        try {
            outputStream = context.contentResolver.openOutputStream(uri!!)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            MediaScannerConnection.scanFile(context,
                arrayOf(getFilePathFromURI(context, uri)),
                arrayOf("image/png"),
                null)
            outputStream?.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /**
     * 保存图片到指定目录，并刷新图库
     * @param context 上下文
     * @param path 保存路径
     * @param bitmap 需要保存的位图
     */
    fun saveBitmap(context: Context, path: String, bitmap: Bitmap) {
        var fos: FileOutputStream?
        var connection by Delegates.notNull<MediaScannerConnection>()
        fos = FileOutputStream(path)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        connection = MediaScannerConnection(context, object :
            MediaScannerConnection.MediaScannerConnectionClient {
            override fun onScanCompleted(path1: String?, uri: Uri?) {
                if (path1.equals(path)) {
                    connection?.disconnect()
                }
            }

            override fun onMediaScannerConnected() {
                connection?.scanFile(path, "image/png")
            }
        })
        connection.connect()
        fos.close()
    }

    /**
     * View转换成Bitmap
     */
    fun convertViewToBitmap(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        return bitmap
    }

    /**
     * View转换成Bitmap
     * 适配Android O+
     *
     * @param targetView     targetView
     * @param getCacheResult 转换成功回调接口
     */
    fun getBitmapFromView(
        @NotNull activity: Activity,
        targetView: View,
        getCacheResult: CacheResult,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //准备一个bitmap对象，用来将copy出来的区域绘制到此对象中
            val bitmap =
                Bitmap.createBitmap(targetView.width, targetView.height, Bitmap.Config.ARGB_8888)
            //获取layout的left-top顶点位置
            val location = IntArray(2)
            targetView.getLocationInWindow(location)
            //请求转换
            PixelCopy.request(activity.window,
                Rect(location[0], location[1],
                    location[0] + targetView.width, location[1] + targetView.height),
                bitmap, { copyResult ->
                    //如果成功
                    if (copyResult == PixelCopy.SUCCESS) {
                        //方法回调
                        getCacheResult.result(bitmap)
                    }
                }, Handler(Looper.getMainLooper()))
        } else {
            targetView.isDrawingCacheEnabled = true
            targetView.buildDrawingCache()
            val drawingCache = targetView.drawingCache
            getCacheResult.result(drawingCache)
            targetView.destroyDrawingCache()
        }
    }
    interface CacheResult{
        fun result(bitmap: Bitmap);
    }
}