package cn.guo.onetext.fragment

import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import cn.guo.onetext.PermissionMethods
import cn.guo.onetext.R
import cn.guo.onetext.Utils
import cn.guo.onetext.alerter.Alerter
import cn.guo.onetext.dialog.BaseInputDialog
import cn.guo.onetext.instance.TextRegister
import cn.guo.onetext.widget.CircleImageView
import cn.guo.onetext.widget.QuadFlaskColorpicker.ColorPickerView
import cn.guo.onetext.widget.QuadFlaskColorpicker.builder.ColorPickerDialogBuilder
import cn.guo.onetext.widget.SvgAnimationView.SvgAnimationView
import java.io.File
import kotlin.math.roundToLong

class CardStyle : BaseFragment() {

    private val TAG = "CardStyle"

    private lateinit var sharedPreference: SharedPreferences

    private lateinit var cardSvg: SvgAnimationView
    private lateinit var cardBackgroundColor: LinearLayout
    private lateinit var cardShot: LinearLayout
    private lateinit var cardImageBackground: LinearLayout
    private lateinit var cardTextContent: TextView
    private lateinit var cardTextContentBottom: TextView
    private lateinit var cardTextContentTop: TextView
    private lateinit var cardTextAuthor: TextView
    private lateinit var cardTextAuthorLeft: TextView
    private lateinit var cardTextDate: TextView
    private lateinit var cardTextSource: TextView
    private lateinit var cardMarkText: TextView
    private lateinit var cardHeart: CircleImageView
    private lateinit var viewGroup: ViewGroup

    private lateinit var style: String
    private lateinit var styles: Array<String>

    private var isFormatAlign = false

    private lateinit var inputDialog: BaseInputDialog

    private val iconMarkChangedUpdate = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getStringExtra("TYPE")) {
                "ICON" -> {
                    val state = intent?.getBooleanExtra("ICON_STATE", false)
                    val name = intent?.getStringExtra("ICON_NAME")
                    if (state) cardSvg.visibility = View.VISIBLE
                    else cardSvg.visibility = View.GONE
                    setSvgData(name.toString())
                }
                "MARK" -> {
                    val state = intent?.getBooleanExtra("MARK_STATE", false)
                    val color = intent?.getIntExtra("MARK_COLOR", 0)
                    val text = intent?.getStringExtra("MARK_TEXT")
                    if (state) cardMarkText.visibility = View.VISIBLE
                    else cardMarkText.visibility = View.GONE
                    setMarkData(text, color)
                }
            }
        }
    }

    private val textChangedForSentence = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            cardTextContent.text = intent?.getStringExtra("SENTENCE")
        }
    }

    private val formatAlignReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getBooleanExtra("FORMAT_ALIGN", false)?.let {
                formatAlign(it)
            }
        }
    }

    private val setTextColors = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getIntExtra("TEXT_COLOR_ALL",
                ContextCompat.getColor(context!!, R.color.colorBlue))?.let {
                setContextTextColors(it)
            }
        }
    }

    private val setTextFont = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getIntExtra("ID", 0)?.let {
                setTextFont(it)
            }
        }
    }

    private val sizeAdjust = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val value = intent?.getFloatExtra("VALUE", 0f)
            when (intent?.getStringExtra("AD_JUST_TYPE")) {
                "HEARD" -> {

                }
                "CONTENT" -> value?.let { textSizeAdjust(cardTextContent, value) }
                "AUTHOR" -> value?.let { textSizeAdjust(cardTextAuthor, value) }
                "DATE" -> value?.let { textSizeAdjust(cardTextDate, value) }
                "SOURCE" -> value?.let { textSizeAdjust(cardTextSource, value) }
            }
        }
    }

    private val selectPictureCall = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bam = resources.getStringArray(R.array.background_mode)
            val mode = sharedPreference.getString(getString(R.string.set_key_background_apply_mode),
                bam[0])
            val bitmap = BitmapFactory.decodeFile(File(activity?.externalCacheDir, "crop.png").absolutePath)
            val bitmapDrawable = BitmapDrawable(resources,bitmap)

            when (mode) {
                bam[0] -> cardImageBackground.background = bitmapDrawable
                bam[1] -> cardBackgroundColor.background = bitmapDrawable
            }
        }
    }

    private val setBackgroundColor = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent?.getBooleanExtra("TYPE", true)
            val bam = resources.getStringArray(R.array.background_mode)
            val mode = sharedPreference.getString(getString(R.string.set_key_background_apply_mode),
                bam[0])
            if (type == true) {
                val value = intent?.getIntExtra("VALUE", ContextCompat.getColor(context!!,
                    R.color.white))
                when (mode) {
                    bam[0] -> cardImageBackground.setBackgroundColor(value!!)
                    bam[1] -> cardBackgroundColor.setBackgroundColor(value!!)
                }
                return
            }

            val view = if (mode == bam[1]) cardBackgroundColor else cardImageBackground
            "SELECT_PICTURE".sendBroadcast(Bundle().apply {
                putBoolean("NEED_CROP", true)
                putInt("WIDTH", view.width)
                putInt("HEIGHT", view.height)
            })
        }
    }

    private fun textSizeAdjust(textView: TextView, textSize: Float) {
        if (textSize != -1f) textView.textSize = textSize
        else textOnClick(textView)
    }

    private val saveImageFile = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val saveType = intent?.getBooleanExtra("isBigImage", false)
            save(saveType == true)
        }
    }

    private fun register() {
        iconMarkChangedUpdate.registerReceiver("ICON_MARK_CHANGED_UPDATE")
        saveImageFile.registerReceiver("SAVE_IMAGE_FILE")
        textChangedForSentence.registerReceiver("TEXT_CHANGED_FOR_SENTENCE")
        sizeAdjust.registerReceiver("SIZE_ADJUST_IT")
        setBackgroundColor.registerReceiver("CARD_BACKGROUND_COLOR")
        formatAlignReceiver.registerReceiver("FORMAT_ALIGN")
        setTextColors.registerReceiver("SET_TEXT_COLOR_ALL")
        setTextFont.registerReceiver("FONT_RESOURCE_FILE")
        selectPictureCall.registerReceiver("PICTURE_SELECTED")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        return getCardLayout(inflater)
    }

    private fun getCardLayout(inflater: LayoutInflater): View {
        styles = resources?.getStringArray(R.array.style_list)
        val layouts = arrayOf(
            R.layout.card_style_1,
            R.layout.card_style_2,
            R.layout.card_style_3,
            R.layout.card_style_4,
            R.layout.card_style_5,
            R.layout.card_style_6,
            R.layout.card_style_7,
            R.layout.card_style_8,
            R.layout.card_style_9,
            R.layout.card_style_10
        )
        style = sharedPreference.getString(
            resources?.getString(R.string.set_key_card_style),
            styles[0]
        ).toString()
        styles.forEachIndexed { index, s ->
            if (s == style)
                return inflater.inflate(layouts[index], null, false)
        }
        return inflater.inflate(R.layout.card_style_1, null, false)
    }

    private fun findViews() {
        view?.apply {
            cardSvg = findViewById(R.id.card_svg)
            cardShot = findViewById(R.id.card_shot)
            cardBackgroundColor = findViewById(R.id.card_background_color)
            cardImageBackground = findViewById(R.id.card_image_background)
            cardTextAuthor = findViewById(R.id.card_text_author)
            cardTextAuthorLeft = findViewById(R.id.card_text_author_felt)
            cardTextContent = findViewById(R.id.card_text_content)
            cardTextContentBottom = findViewById(R.id.card_text_content_bottom)
            cardTextContentTop = findViewById(R.id.card_text_content_top)
            cardTextDate = findViewById(R.id.card_text_date)
            cardTextSource = findViewById(R.id.card_text_source)
            cardMarkText = findViewById(R.id.card_mark_text)
            cardHeart = findViewById(R.id.card_heart)
            viewGroup = cardShot?.getChildAt(0) as ViewGroup
        }
    }

    private fun setMarkData(text: String? = getString(R.string.default_mark), color: Int) {
        if (text != getString(R.string.default_mark) && text != null)
            cardMarkText.text = getString(R.string.default_mark_text) + text
        if (color != 0) cardMarkText.setTextColor(color)
    }

    fun selectPicture() {

        val bam = resources.getStringArray(R.array.background_mode)
        val mode =
            sharedPreference.getString(getString(R.string.set_key_background_apply_mode), bam[0])
        val view = if (mode == bam[1]) cardBackgroundColor else cardShot
    }

    private fun formatAlign(isChecked: Boolean) {
        isFormatAlign = isChecked
        if (isChecked) {
            val arr = cardTextContent.text.toString().split("\n")
            val buffer = StringBuffer()
            for (str in arr) {
                if (buffer.toString() == "")
                    buffer.append("\u3000\u3000" + str)
                else buffer.append("\n\u3000\u3000${str}")
            }
            cardTextContent.text = buffer.toString()
        } else
            cardTextContent.text = replaceSpace()

    }

    private fun setTextFont(id: Int) {
        val font =
            intArrayOf(R.font.font_1, R.font.font_2, R.font.font_3, R.font.font_4, R.font.font_5,
                R.font.font_6, R.font.font_7, R.font.font_8, R.font.font_9, R.font.font_10)
        val arr =
            arrayOf(cardTextContent,
                cardTextContentTop,
                cardTextContentBottom,
                cardTextAuthor,
                cardTextAuthorLeft,
                cardTextDate,
                cardTextSource,
                cardMarkText)

        val typeface = if (id == 0) Typeface.defaultFromStyle(Typeface.NORMAL) else context?.let {
            ResourcesCompat.getFont(
                it,
                font[id - 1])
        }
        arr.forEach {
            it.typeface = typeface
        }

    }

    private fun setSvgData(name: String) {
        when {
            name != "" -> cardSvg.tag = name
            cardSvg.tag.toString() == "" -> return
            else -> return
        }
        cardSvg.apply {
            startSvgDataResource(tag.toString())
            setOnStateChangeListener { state ->
                if (state == SvgAnimationView.STATE_FINISHED);
            }
            setViewportSize(1024f, 1024f)
            setTraceResidueColor(0x22000000)
            setFillTime(2000)
            rebuildGlyphData()
            start()
        }
    }

    private fun save(type: Boolean) {
        if (!PermissionMethods.checkPermission(context, PermissionMethods.PERMISSIONS_STORAGE)) {
            PermissionMethods.storagePermissionDialog(activity)
            return
        }
        val view = if (type) cardBackgroundColor else cardShot
        val bitmap = Utils.convertViewToBitmap(view)

        val name = "oneText_" + System.currentTimeMillis() + ".png"
        var state = "成功"
        if(sharedPreference.getBoolean(getString(R.string.set_key_save_state),true))
        state = if (Utils.saveBitmapToPicture(requireActivity(), bitmap, name)) "成功" else "失败"
        Utils.saveBitmap(requireContext(),File(activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),name).absolutePath,bitmap!!)
        "IMAGE_SAVED".sendBroadcast()
        Alerter.create(requireActivity())
            .setText("保存${state}")
            .setBackgroundColorRes(R.color.colorBlue)
            .show()
        recycle(bitmap)
    }

    private fun recycle(bitmap: Bitmap?) {
        if (bitmap != null && !bitmap.isRecycled)
            bitmap.recycle()
    }

    private fun loadCardData() {
        setSvgData("svg/花.svg")
        loadCardText()
    }

    private fun loadCardText() {
        val defaultAuthor = resources.getStringArray(R.array.textDefaultAuthor)
        val defaultContent = resources.getStringArray(R.array.textDefaultContent)
        val defaultDate = resources.getStringArray(R.array.textDefaultDate)
        val defaultSource = resources.getStringArray(R.array.textDefaultSource)
        val follow =
            sharedPreference.getBoolean(getString(R.string.set_key_text_content_mode), true)
        styles.forEachIndexed { index, s ->
            if (style == s) {
                cardTextContent.text =
                    if (TextRegister.content != "" && follow) TextRegister.content else defaultContent[index]
                cardTextAuthor.text =
                    if (TextRegister.author != "" && follow) TextRegister.author else defaultAuthor[index]
                cardTextSource.text =
                    if (TextRegister.source != "" && follow) TextRegister.source else defaultSource[index]
                cardTextDate.text =
                    if (TextRegister.date != "" && follow) TextRegister.date else defaultDate[index]
                return
            }
        }
        cardMarkText.setText(R.string.default_mark)
    }

    //触摸点在屏幕上的横纵坐标
    private var mScreenY: Float = 0.0f

    //上次触摸的横纵坐标
    private var mLastY: Float = 0.0f

    //按下的横纵坐标
    private var mDownY: Float = 0.0f

    private fun moveCard() {
        cardBackgroundColor.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                mScreenY = event?.rawY!!
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> mDownY = mScreenY
                    MotionEvent.ACTION_MOVE -> updateViewPosition()
                    MotionEvent.ACTION_UP -> updateViewPosition()
                }
                mLastY = mScreenY
                return true
            }
        })
    }

    private fun updateViewPosition() {
        var y = (cardShot.y + mScreenY - mLastY).roundToLong().toFloat()
        val limit: Float = (cardBackgroundColor.measuredHeight - cardShot.measuredHeight).toFloat()
        if (y < 0) y = 0f else if (y > limit) y = limit
        cardShot.y = y
    }

    private fun textOnClick(textView: TextView) {
        var dialog: AlertDialog
        var edit: EditText
        context?.let {
            dialog = AlertDialog.Builder(it).apply {
                val layout = LayoutInflater.from(context).inflate(
                    R.layout.dialog_input,
                    null,
                    false
                )

                edit = layout.findViewById(R.id.edit_query)

                val color = textView.paint.color
                if (textView.tag == null) {
                    textView.tag = color
                }

                edit.tag = color

                if (textView == cardTextContent)
                    edit.setText(replaceSpace())
                else edit.setText(textView.text)

                edit.setTextColor(color)

                setView(layout)
                setTitle("文本/颜色")
                setPositiveButton("应用", null)
                setNeutralButton("取消", null)
                setNegativeButton("字体颜色", null)
                setCancelable(false)
            }.create()
            dialog.setOnShowListener {
                dialog.apply {
                    getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        if (TextUtils.isEmpty(edit.text)) {
                            "设置空文本可从“文本”选项卡打开".showAlerter()
                        } else {
                            val text = edit.text.toString()
                            textView.text = text
                            formatAlign(isFormatAlign)
                            when (textView) {
                                cardTextAuthor -> TextRegister.author = text
                                cardTextContent -> TextRegister.content = text
                                cardTextDate -> TextRegister.date = text
                                cardTextSource -> TextRegister.source = text
                            }
                        }
                        setContextTextColor(edit.tag as Int, textView)
                        textView.tag = edit.tag
                        dialog.dismiss()
                    }
                    getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                        selectTextColor(edit)
                    }
                    getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
                        edit.tag = textView.tag
                        dialog.dismiss()
                    }
                }
            }
            dialog.show()

        }
    }

    private fun replaceSpace(): String {
        return cardTextContent.text.toString()
            .replace("\u3000\u3000", "")
            .replace("\n\u3000\u3000", "\n")
    }

    private fun setContextTextColor(color: Int, textView: TextView) {
        textView.setTextColor(color)
        when (textView) {
            cardTextContent -> {
                cardTextContentTop.setTextColor(color)
                cardTextContentBottom.setTextColor(color)
                if (style == styles[6])
                    (view?.findViewById<View>(R.id.tag) as ImageView).setColorFilter(color)
            }

            cardTextAuthor -> {
                cardTextAuthorLeft.setTextColor(color)
                cardTextAuthor.setTextColor(color)
            }
        }
    }

    private fun setContextTextColors(color: Int) {
        arrayOf(cardTextAuthor,
            cardTextAuthorLeft,
            cardTextContent,
            cardTextContentTop,
            cardTextContentBottom,
            cardTextSource,
            cardTextDate).forEach {
            it.setTextColor(color)
        }
        if (style == styles[6]) {
            (view?.findViewById<View>(R.id.tag) as ImageView).setColorFilter(color)
        }
    }

    private fun selectTextColor(editText: EditText) {
        ColorPickerDialogBuilder.with(context).apply {
            setTitle("字体颜色")
            initialColor(editText.tag as Int)
            wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            density(12)
            setOnColorSelectedListener {

            }
            setPositiveButton("应用") { dialog, selectedColor, allColors ->
                editText.setTextColor(selectedColor)
                editText.tag = selectedColor
            }
            setNegativeButton("取消", null)
            build().show()
        }
    }

    private fun initObject() {
        inputDialog = BaseInputDialog(requireContext())
    }

    private fun setListener() {
        moveCard()
        arrayOf(cardTextAuthor, cardTextDate, cardTextSource, cardTextContent).forEach {
            it.apply {
                setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> textOnClick(v as TextView)
                    }
                    true
                }
                setOnClickListener {
                    textOnClick(it as TextView)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.apply {
            sendBroadcast(Intent("${this.packageName}.CARD_LOADED"))
        }
        initObject()
        findViews()
        loadCardData()
        setListener()
        register()
    }

}