package cn.guo.onetext.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat

import cn.guo.onetext.R
import cn.guo.onetext.instance.FontSelect
import cn.guo.onetext.instance.SentenceSelect
import cn.guo.onetext.preference.BasePreference
import cn.guo.onetext.preference.SeekBarPreference
import cn.guo.onetext.widget.QuadFlaskColorpicker.ColorPickerView
import cn.guo.onetext.widget.QuadFlaskColorpicker.builder.ColorPickerDialogBuilder
import kotlin.properties.Delegates

class SetText : BasePreference() {

    private val TAG = "SetText"
    private lateinit var defaultSizeAuthor: IntArray
    private lateinit var defaultSizeContent: IntArray
    private lateinit var defaultSizeDate: IntArray
    private lateinit var defaultSizeSource: IntArray
    private lateinit var defaultTitleAuthor: Array<String>
    private lateinit var defaultTitleContent: Array<String>
    private lateinit var defaultTitleDate: Array<String>
    private lateinit var defaultTitleSource: Array<String>
    private lateinit var styles: Array<String>
    private lateinit var style: String

    private var color by Delegates.notNull<Int>()
    private var index by Delegates.notNull<Int>()

    private val cardLoaded = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getStyle()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.set_text, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        getStyle()
        cardLoaded.registerReceiver("CARD_LOADED")
    }

    private fun getStyle() {
        style = PreferenceManager.getDefaultSharedPreferences(context).getString(
            resources?.getString(R.string.set_key_card_style),
            styles[0]
        ).toString()
        index = styles.indexOf(style)
        setListener()
    }

    private fun loadData() {
        styles = resources?.getStringArray(R.array.style_list)

        defaultSizeAuthor = resources.getIntArray(R.array.textDefaultSizeAuthor)
        defaultSizeContent = resources.getIntArray(R.array.textDefaultSizeContent)
        defaultSizeDate = resources.getIntArray(R.array.textDefaultSizeDate)
        defaultSizeSource = resources.getIntArray(R.array.textDefaultSizeSource)

        defaultTitleAuthor = resources.getStringArray(R.array.textDefaultTitleAuthor)
        defaultTitleContent = resources.getStringArray(R.array.textDefaultTitleContent)
        defaultTitleDate = resources.getStringArray(R.array.textDefaultTitleDate)
        defaultTitleSource = resources.getStringArray(R.array.textDefaultTitleSource)

        color = ContextCompat.getColor(context!!,R.color.colorBlue)
    }

    private fun setListener() {
        findPreference<Preference>(getString(R.string.set_key_text_sentence))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                SentenceSelect.get(activity).apply {
                    SentenceSelect.show()
                }
                true
            }
        }
        findPreference<Preference>(getString(R.string.set_key_text_font))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                FontSelect.get(activity).apply {
                    FontSelect.show()
                }
                true
            }
        }
        findPreference<Preference>(getString(R.string.set_key_text_color))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                selectTextColor()
                true
            }
        }
        findPreference<SeekBarPreference>(getString(R.string.set_key_text_size_content))?.apply {
            onClick("CONTENT", this)
            value = defaultSizeContent[index]
            isVisible = defaultSizeContent[index] != 0
            title = defaultTitleContent[index]
        }
        findPreference<SeekBarPreference>(getString(R.string.set_key_text_size_author))?.apply {
            onClick("AUTHOR", this)
            value = defaultSizeAuthor[index]
            isVisible = defaultSizeAuthor[index] != 0
            title = defaultTitleAuthor[index]
        }
        findPreference<SeekBarPreference>(getString(R.string.set_key_text_size_time))?.apply {
            onClick("DATE", this)
            value = defaultSizeDate[index]
            isVisible = defaultSizeDate[index] != 0
            title = defaultTitleDate[index]
        }
        findPreference<SeekBarPreference>(getString(R.string.set_key_text_size_from))?.apply {
            onClick("SOURCE", this)
            value = defaultSizeSource[index]
            isVisible = defaultSizeSource[index] != 0
            title = defaultTitleSource[index]
        }
        findPreference<SwitchPreferenceCompat>(getString(R.string.set_key_text_format_align))?.apply {
            onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newState ->

                    "FORMAT_ALIGN".sendBroadcast(Bundle().apply {
                        putBoolean("FORMAT_ALIGN", newState == true)
                    })

                    true
                }
        }
    }

    private fun onClick(type: String, seek: SeekBarPreference) {
        seek.setListener {
            sendValue(type, it.toFloat())
        }
        seek.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            sendValue(type, -1f)
            true
        }
    }

    private fun sendValue(type: String, value: Float) {
        "SIZE_ADJUST_IT".sendBroadcast(Bundle().apply {
            putString("AD_JUST_TYPE", type)
            putFloat("VALUE", value)
        })
    }

    private fun selectTextColor() {
        ColorPickerDialogBuilder.with(context).apply {
            setTitle("字体颜色")
            initialColor(color)
            wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            density(12)
            setOnColorSelectedListener {

            }
            setPositiveButton("应用") { dialog, selectedColor, allColors ->
                color=selectedColor
                "SET_TEXT_COLOR_ALL".sendBroadcast(Bundle().apply {
                    putInt("TEXT_COLOR_ALL",selectedColor)
                })
            }
            setNegativeButton("取消", null)
            build().show()
        }
    }
}