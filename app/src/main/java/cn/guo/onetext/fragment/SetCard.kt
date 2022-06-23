package cn.guo.onetext.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import cn.guo.onetext.R
import cn.guo.onetext.preference.BasePreference
import cn.guo.onetext.widget.QuadFlaskColorpicker.ColorPickerView
import cn.guo.onetext.widget.QuadFlaskColorpicker.builder.ColorPickerDialogBuilder
import rikka.preference.SimpleMenuPreference
import kotlin.properties.Delegates

class SetCard : BasePreference() {

    private val TAG = "SetCard"

    private lateinit var styles:Array<String>
    private lateinit var style:String
    private var backgroundColor by Delegates.notNull<Int>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.set_card, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        updateConfig(style)
        setListener()
    }

    private fun initData(){
        styles = resources?.getStringArray(R.array.style_list)
        style = PreferenceManager.getDefaultSharedPreferences(context).getString(getString(R.string.set_key_card_style),styles[0])
            .toString()
        backgroundColor = ContextCompat.getColor(context!!,R.color.white)
    }

    private fun setListener(){
        findPreference<SimpleMenuPreference>(getString(R.string.set_key_card_style))?.apply {
            setOnPreferenceChangeListener { preference, newValue ->
                "CARD_CHANGED_UPDATE".sendBroadcast(null)
                updateConfig(newValue.toString())
                true
            }
        }
        findPreference<Preference>(getString(R.string.set_key_background_color))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                selectBackground(true)
                true
            }
        }
        findPreference<Preference>(getString(R.string.set_key_background_image))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                selectBackground(false)
                true
            }
        }
    }

    private fun selectBackground(isColor:Boolean) {
        if(!isColor){
            "CARD_BACKGROUND_COLOR".sendBroadcast(Bundle().apply {
                putBoolean("TYPE",isColor)
            })
            return
        }
        ColorPickerDialogBuilder.with(context).apply {
            setTitle("背景颜色")
            initialColor(backgroundColor)
            wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            density(12)
            setOnColorSelectedListener {

            }
            setPositiveButton("应用") { dialog, selectedColor, allColors ->
                "CARD_BACKGROUND_COLOR".sendBroadcast(Bundle().apply {
                    putInt("VALUE",selectedColor)
                    putBoolean("TYPE",isColor)
                })
                backgroundColor = selectedColor
            }
            setNegativeButton("取消", null)
            build().show()
        }
    }

    private fun updateConfig(style:String){
        val displayHeart = resources.getStringArray(R.array.display_head)
        styles.forEachIndexed { index, s ->
            if(style==s){
                findPreference<SwitchPreferenceCompat>(getString(R.string.set_key_card_heart_root))?.apply {
                    isEnabled=displayHeart[index].toBoolean()
                }
            }
        }
    }

}