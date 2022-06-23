package cn.guo.onetext.activity

import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import cn.guo.onetext.databinding.ActivityClipImageBinding
import java.io.Closeable
import java.io.FileOutputStream
import java.io.IOException

/**
 * @解决兼容性
 */
class ClipImageActivity : BaseBindingActivity<ActivityClipImageBinding>() {

    // 图片被旋转的角度
    private var mDegree = 0

    // 大图被设置之前的缩放比例
    private var mSampleSize = 0
    private var mSourceWidth = 0
    private var mSourceHeight = 0

    private var mOutput: String? = null
    private var mInput: String? = null
    private var mMaxWidth = 0
    private lateinit var dialog:AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        initBinding(ActivityClipImageBinding.inflate(layoutInflater))
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initData()
        //大图裁剪
        setImageAndClipParams()
    }

    private fun initData() {
        val clipOptions = Clip.createFromBundle(intent)
        mOutput = clipOptions.outputPath
        mInput = clipOptions.inputPath
        mMaxWidth = clipOptions.maxWidth
        binding.clipImageView.apply {
            setAspect(clipOptions.aspectX, clipOptions.aspectY)
            setTip(clipOptions.tip)
            setMaxOutputWidth(mMaxWidth)
        }
        dialog = AlertDialog.Builder(this).apply {
            setTitle("裁剪中")
            setMessage("请稍等...")
            setCancelable(false)
        }.create()
    }

    private fun setImageAndClipParams() {
        binding.clipImageView.post {
            binding.clipImageView.setMaxOutputWidth(mMaxWidth)
            mDegree = readPictureDegree(mInput)
            val isRotate = mDegree == 90 || mDegree == 270
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(mInput, options)
            mSourceWidth = options.outWidth
            mSourceHeight = options.outHeight

            // 如果图片被旋转，则宽高度置换
            val width = if (isRotate) options.outHeight else options.outWidth

            // 裁剪是宽高比例3:2，只考虑宽度情况，这里按border宽度的两倍来计算缩放。
            mSampleSize = findBestSample(width, binding.clipImageView.clipBorder.width())
            options.apply {
                inJustDecodeBounds = false
                inSampleSize = mSampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val source = BitmapFactory.decodeFile(mInput, options)

            // 解决图片被旋转的问题
            val target: Bitmap
            if (mDegree == 0) {
                target = source
            } else {
                val matrix = Matrix()
                matrix.postRotate(mDegree.toFloat())
                target = Bitmap
                    .createBitmap(source,
                        0, 0, source.width, source.height, matrix, false)
                if (target != source && !source.isRecycled) {
                    source.recycle()
                }
            }
            binding.clipImageView.setImageBitmap(target)
        }
    }

    /**
     * 计算最好的采样大小。
     * @param origin 当前宽度
     * @param target 限定宽度
     * @return sampleSize
     */
    private fun findBestSample(origin: Int, target: Int): Int {
        var sample = 1
        var out = origin / 2
        while (out > target) {
            sample *= 2
            out /= 2
        }
        return sample
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    private fun readPictureDegree(path: String?): Int {
        var degree = 0
        val exifInterface = ExifInterface(path!!)
        val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
        }
        return degree
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("确定")?.apply {
            setOnMenuItemClickListener {
                clipImage()
                true
            }
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun clipImage() {
        if (mOutput != null) {
            dialog.show()
            val task: AsyncTask<Void?, Void?, Void?> = object : AsyncTask<Void?, Void?, Void?>() {
                override fun doInBackground(vararg params: Void?): Void? {
                    var fos: FileOutputStream? = null
                    try {
                        fos = FileOutputStream(mOutput)
                        val bitmap = createClippedBitmap()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                        if (!bitmap.isRecycled) {
                            bitmap.recycle()
                        }
                        setResult(RESULT_OK, intent)
                    } catch (e: Exception) {
                        Log.e(TAG, "doInBackground: 无法保存图片", )
                    } finally {
                        close(fos)
                    }
                    return null
                }

                override fun onPostExecute(aVoid: Void?) {
                    dialog.dismiss()
                    "PICTURE_SELECTED".sendBroadcast()
                    finish()
                }
            }
            task.execute()
        } else {
            finish()
        }
    }

    fun close(c: Closeable?) {
        if (c != null) {
            try {
                c.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createClippedBitmap(): Bitmap {
        if (mSampleSize <= 1) {
            return binding.clipImageView.clip()
        }
        // 获取缩放位移后的矩阵值
        val matrixValues: FloatArray = binding.clipImageView.clipMatrixValues
        val scale = matrixValues[Matrix.MSCALE_X]
        val transX = matrixValues[Matrix.MTRANS_X]
        val transY = matrixValues[Matrix.MTRANS_Y]

        // 获取在显示的图片中裁剪的位置
        val border: Rect = binding.clipImageView.clipBorder
        val cropX = (-transX + border.left) / scale * mSampleSize
        val cropY = (-transY + border.top) / scale * mSampleSize
        val cropWidth = border.width() / scale * mSampleSize
        val cropHeight = border.height() / scale * mSampleSize

        // 获取在旋转之前的裁剪位置
        val srcRect = RectF(cropX, cropY, cropX + cropWidth, cropY + cropHeight)
        val clipRect = getRealRect(srcRect)
        val ops = BitmapFactory.Options()
        val outputMatrix = Matrix()
        outputMatrix.setRotate(mDegree.toFloat())
        // 如果裁剪之后的图片宽高仍然太大,则进行缩小
        if (mMaxWidth > 0 && cropWidth > mMaxWidth) {
            ops.inSampleSize =
                findBestSample(cropWidth.toInt(),
                    mMaxWidth)
            val outputScale = mMaxWidth / (cropWidth / ops.inSampleSize)
            outputMatrix.postScale(outputScale, outputScale)
        }
        // 裁剪
        var decoder: BitmapRegionDecoder? = null
        return try {
            decoder = BitmapRegionDecoder.newInstance(mInput, false)
            val source = decoder.decodeRegion(clipRect, ops)
            recycleImageViewBitmap()
            Bitmap.createBitmap(source, 0, 0, source.width, source.height, outputMatrix, false)
        } catch (e: Exception) {
            binding.clipImageView.clip()
        } finally {
            if (decoder != null && !decoder.isRecycled) {
                decoder.recycle()
            }
        }
    }

    private fun getRealRect(srcRect: RectF): Rect {
        return when (mDegree) {
            90 -> Rect(srcRect.top.toInt(),
                (mSourceHeight - srcRect.right).toInt(),
                srcRect.bottom.toInt(), (mSourceHeight - srcRect.left).toInt())
            180 -> Rect((mSourceWidth - srcRect.right).toInt(),
                (mSourceHeight - srcRect.bottom).toInt(),
                (mSourceWidth - srcRect.left).toInt(), (mSourceHeight - srcRect.top).toInt())
            270 -> Rect((mSourceWidth - srcRect.bottom).toInt(),
                srcRect.left.toInt(),
                (mSourceWidth - srcRect.top).toInt(), srcRect.right.toInt())
            else -> Rect(srcRect.left.toInt(),
                srcRect.top.toInt(), srcRect.right.toInt(), srcRect.bottom.toInt())
        }
    }

    private fun recycleImageViewBitmap() {
        binding.clipImageView.post { binding.clipImageView.setImageBitmap(null) }
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED, intent)
        super.onBackPressed()
    }

    class ClipOptions() {
        var aspectX = 0
        var aspectY = 0
        var maxWidth = 0
        var tip: String? = null
        var inputPath: String? = null
        var outputPath: String? = null

        fun aspectX(aspectX: Int): ClipOptions {
            this.aspectX = aspectX
            return this
        }

        fun aspectY(aspectY: Int): ClipOptions {
            this.aspectY = aspectY
            return this
        }

        fun maxWidth(maxWidth: Int): ClipOptions {
            this.maxWidth = maxWidth
            return this
        }

        fun tip(tip: String?): ClipOptions {
            this.tip = tip
            return this
        }

        fun inputPath(path: String?): ClipOptions {
            inputPath = path
            return this
        }

        fun outputPath(path: String?): ClipOptions {
            outputPath = path
            return this
        }

        fun startForResult(activity: BaseActivity, requestCode: Int) {
            checkValues()
            val intent = Intent(activity, ClipImageActivity::class.java).apply {
                putExtra("aspectX", aspectX)
                putExtra("aspectY", aspectY)
                putExtra("maxWidth", maxWidth)
                putExtra("tip", tip)
                putExtra("inputPath", inputPath)
                putExtra("outputPath", outputPath)
            }
            activity.startActivityForResult(intent, requestCode)
        }

        private fun checkValues() {
            require(!TextUtils.isEmpty(inputPath)) { "The input path could not be empty" }
            require(!TextUtils.isEmpty(outputPath)) { "The output path could not be empty" }
        }
    }

    object Clip {
        fun prepare(): ClipOptions {
            return ClipOptions()
        }
        fun createFromBundle(intent: Intent): ClipOptions {
            return ClipOptions()
                .aspectX(intent.getIntExtra("aspectX", 1))
                .aspectY(intent.getIntExtra("aspectY", 1))
                .maxWidth(intent.getIntExtra("maxWidth", 0))
                .tip(intent.getStringExtra("tip"))
                .inputPath(intent.getStringExtra("inputPath"))
                .outputPath(intent.getStringExtra("outputPath"))
        }
    }
}