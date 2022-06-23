package cn.guo.onetext

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.core.content.FileProvider
import java.io.*
import kotlin.properties.Delegates

class PictureManager {

    private val TAG = "PictureManager"

    object StateCode {
        val CHOOSE_PHOTO_CODE = 1000 //选择相册
        val PICTURE_CROP_CODE = 2000 //剪切图片
        val REQUEST_PERMISSIONS = 3000 //授权code
    }

    object Utils {
        /**
         * 打开系统的设置界面 让用户自己授权
         * @param context
         * @return
         */
        fun getSettingIntent(context: Context): Intent? =
            Intent("android.settings.APPLICATION_DETAILS_SETTINGS").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = Uri.fromParts("package", context.packageName, null)
            }

        /**
         * 获取uri对应的真实路径
         * @param context
         * @param uri
         * @param selection
         * @return
         */
        fun getImagePath(context: Context, uri: Uri?, selection: String?): String? {
            var path: String? = null
            // 通过Uri和selection来获取真实的图片路径
            val cursor = context.contentResolver.query(uri!!, null, selection, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                }
                cursor.close()
            }
            return path
        }

        /**
         * 4.4以上获取相册的地址 相册图片返回的uri是经过系统封装过的
         * @param context
         * @param uri
         * @return
         */
        fun getFilePathByUri(context: Context?, uri: Uri?): String? {
            var imagePath: String? = null
            if (context == null || uri == null) {
                return imagePath
            }
            when {
                // 如果是document类型的Uri，则通过document id处理
                DocumentsContract.isDocumentUri(context, uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    when (uri.authority) {
                        "com.android.providers.media.documents" -> {
                            // 解析出数字格式的id
                            val id = docId.split(":")[1]
                            val selection = MediaStore.Images.Media._ID + "=" + id
                            imagePath = getImagePath(context,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
                        }
                        "com.android.providers.downloads.documents" -> {
                            val contentUri =
                                ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"),
                                    docId.toLong())
                            imagePath = getImagePath(context, contentUri, null)
                        }
                    }
                }
                // 如果是content类型的Uri，则使用普通方式处理
                "content" == uri.scheme ->
                    imagePath = getImagePath(context, uri, null)
                // 如果是file类型的Uri，直接获取图片路径即可
                "file" == uri.scheme -> imagePath = uri.path
            }
            return imagePath
        }

        /**
         * 将文件复制到app私有cache目录
         * @param context
         * @param srcUri 源文件URI
         * @param srcUri 目标文件
         */
        fun copyFile(context: Context, srcUri: Uri?, dstFile: File?) {
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

        private fun copyStream(input: InputStream?, output: OutputStream?): Int {
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
    }

    //两个比例略微不一样是为了解决部分手机1:1时显示的时圆形裁剪框
    private var aspectX = 1000//裁剪的宽高比例
    private var aspectY = 1001//裁剪的宽高比例

    private val outputX = 400 //裁剪后输出图片的尺寸大小
    private val outputY = 400 //裁剪后输出图片的尺寸大小

    private var isNeedCrop = false //是否需要裁剪 默认不需要
    private var isScale = true //是否需要支持缩放 在可裁剪情况下有效

    private var activity by Delegates.notNull<Activity>()
    private var listener by Delegates.notNull<PictureSelectListener>()

    //文件名
    private val fileName = "one_text_temp.png"

    //拍照裁剪时的原图片 需要删掉
    //private var oldFileName: String? = null

    //相机拍照图片保存地址
    private var outImageUri: Uri? = null

    interface PictureSelectListener {
        fun onPictureSelect(imagePath: String?) //返回图片的路径
        fun throwError(e: Exception?) //当错误时返回对应的异常
    }

    init {
    }

    constructor(activity: Activity) {
        this.activity = activity
    }

    constructor(activity: Activity, listener: PictureSelectListener) {
        this.activity = activity
        this.listener = listener
    }

    /**
     * 设置图片回调监听
     * @param pictureSelectListener
     */
    fun setPictureSelectListener(pictureSelectListener: PictureSelectListener) {
        listener = pictureSelectListener
    }

    /**
     * 设置是否需要裁剪
     * @param
     * @return
     */
    fun setNeedCrop(isNeedCrop: Boolean): PictureManager? {
        this.isNeedCrop = isNeedCrop
        return this
    }

    /**
     * 设置裁剪是否需要缩放
     * @param isScale
     * @return
     */
    fun setScaleAble(isScale: Boolean): PictureManager? {
        this.isScale = isScale
        return this
    }

    /**
     * 设置裁剪的宽高比例
     *
     * @param x 裁剪比例宽
     * @param y 裁剪比例高
     * @return
     */
    fun setAspect(x: Int, y: Int): PictureManager? {
        this.aspectX = x
        this.aspectY = y
        return this
    }
    fun getAspectX():Int = aspectX
    fun getAspectY():Int = aspectY

    /**
     * 从相册选择
     */
    fun choosePhoto() {
        val intent = Intent("android.intent.action.GET_CONTENT").apply {
            type = "image/*"
        }
        //打开相册
        activity.startActivityForResult(intent, StateCode.CHOOSE_PHOTO_CODE)
    }

    /**
     * 裁剪图片
     * @param pictureUri
     */
    private fun cropPicture(pictureUri: Uri) {
        //从相册先择时，是没有初始化要保存的文件路径以及对应的uri
        initSavedFile(true)
        val cropIntent = Intent("com.android.camera.action.CROP").apply {
            //7.0以上 输入的uri需要是provider提供的
            setDataAndType(pictureUri, "image/*")

            // 开启裁剪：打开的Intent所显示的View可裁剪
            putExtra("crop", "true")
            // 裁剪宽高比
            putExtra("aspectX", aspectX)
            putExtra("aspectY", aspectY)
            // 裁剪输出大小
            putExtra("outputX", outputX)
            putExtra("outputY", outputY)
            putExtra("scale", isScale)
            /**
             * return-data
             * 这个属性决定onActivityResult 中接收到的是什么数据类型，
             * true data将会返回一个bitmap
             * false，则会将图片保存到本地并将我们指定的对应的uri。
             */
            // 当 return-data 为 false 的时候需要设置输出的uri地址
            putExtra("return-data", false)
            //输出的uri为普通的uri，通过provider提供的uri会出现无法保存的错误
            putExtra(MediaStore.EXTRA_OUTPUT, outImageUri)
            // 图片输出格式
            putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
            //不加会出现无法加载此图片的错误
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // 这两句是在7.0以上版本当targeVersion大于23时需要
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        activity.startActivityForResult(cropIntent, StateCode.PICTURE_CROP_CODE)
    }

    /**
     * 先初始化好要保存的文件以及对应的uri
     * @param isCrop 是否需要裁剪
     */
    private fun initSavedFile(isCrop: Boolean) {
        if (outImageUri != null) {
            //已经存在
            return
        }
        //获取app私有目录,不存在则自动创建
        //storage/emulated/0/Android/data/包名/cache
        val cachePath = activity.externalCacheDir?.absolutePath
        //storage/emulated/0/Android/data/包名/cache/one_text_temp.png
        val outputImage = File(cachePath, fileName)

        //如果存在则先删除
        if (outputImage.exists()) outputImage.delete()

        //以下uri的处理在targetVersion大于23时，同时在7.0版本以上时需要做区分
        outImageUri =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || isCrop) {
                //裁剪的时候 保存需要使用一般的uri 要不然出现无法保存裁剪的图片的错误
                Uri.fromFile(outputImage)
            } else {
                //Android 7.0系统开始 使用本地真实的Uri路径不安全,
                //使用FileProvider封装共享Uri(相机拍照输出的uri)
                FileProvider.getUriForFile(activity,
                    activity.packageName + ".fileprovider", outputImage)
            }
    }

    /**
     * 收到图片结果后的处理逻辑
     * @param requestCode
     * @param resultCode
     * @param data
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null || listener == null || resultCode != Activity.RESULT_OK) return
        val imageFile = File(activity.externalCacheDir, fileName)
        Log.d(TAG, "onActivityResult: " + imageFile.absolutePath)
        //需要裁剪时输入的uri
        var inImageUri: Uri?
        when (requestCode) {
            //从相册选择
            StateCode.CHOOSE_PHOTO_CODE -> {
                Log.d(TAG, "onActivityResult: 选择完毕")
                val uri = data.data
                val filePath = Utils.getFilePathByUri(activity, uri)
                Log.d(TAG, "onActivityResult: filePath=${filePath}")
                if (TextUtils.isEmpty(filePath)) {
                    listener.throwError(NullPointerException("没有找到对应的路径"))
                    return
                }
                var cropFile by Delegates.notNull<File>()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Log.d(TAG, "onActivityResult: 复制文件")
                    cropFile = File(activity.externalCacheDir, "clone.png")
                    Utils.copyFile(activity, Uri.fromFile(File(filePath)), cropFile)
                } else cropFile = File(filePath)
                Log.d(TAG, "onActivityResult: ${cropFile.absolutePath},${cropFile.exists()}")
                inImageUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Uri.fromFile(cropFile)
                } else {
                    //Android 7.0系统开始 使用本地真实的Uri路径不安全,使用FileProvider封装共享Uri
                    FileProvider.getUriForFile(activity,
                        "${activity.packageName}.fileprovider",
                        cropFile)
                }
                Log.d(TAG, "onActivityResult: ${inImageUri}")

                /*if (isNeedCrop) {
                    cropPicture(inImageUri)
                } else {*/
                    listener.onPictureSelect(cropFile.absolutePath)
                //}
            }
            //图片裁剪
            StateCode.PICTURE_CROP_CODE -> {

                val cropFile = Utils.getFilePathByUri(activity, outImageUri)

                Log.d(TAG, "onActivityResult: 裁剪完毕=${cropFile},${File(cropFile).exists()}")

                if (TextUtils.isEmpty(cropFile)) {
                    listener.throwError(NullPointerException("没有找到对应的路径"))
                } else {
                    listener.onPictureSelect(cropFile)
                }
            }
        }
    }

}