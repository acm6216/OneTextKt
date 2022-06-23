package cn.guo.onetext.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import cn.guo.onetext.R
import cn.guo.onetext.instance.IconSelect
import cn.guo.onetext.preference.BasePreference
import cn.guo.onetext.widget.QuadFlaskColorpicker.ColorPickerView
import cn.guo.onetext.widget.QuadFlaskColorpicker.builder.ColorPickerDialogBuilder

class SetOther : BasePreference() {

    private lateinit var styles: Array<String>
    private lateinit var displayIcon: Array<String>

    private val configUpdate = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConfig(getStyle())
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.set_other, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        styles = resources?.getStringArray(R.array.style_list)
        displayIcon = resources?.getStringArray(R.array.display_icon)
        updateConfig(getStyle())
        setListener()
        configUpdate.registerReceiver("CARD_CHANGED_UPDATE")
    }

    private fun getStyle(): String = PreferenceManager.getDefaultSharedPreferences(activity)
        .getString(getString(R.string.set_key_card_style), styles[0]).toString()

    private fun updateConfig(style: String) {
        styles.forEachIndexed { index, s ->
            if (style == s) {
                findPreference<SwitchPreferenceCompat>(getString(R.string.set_key_other_icon))?.apply {
                    isEnabled = displayIcon[index].toBoolean()
                }
            }
        }
    }

    private fun setListener() {
        findPreference<SwitchPreferenceCompat>(getString(R.string.set_key_other_icon))?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                "ICON_MARK_CHANGED_UPDATE".sendBroadcast(Bundle().apply {
                    putString("TYPE", "ICON")
                    putBoolean("ICON_STATE", newValue == true)
                    putString("ICON_NAME", "")
                })
                true
            }
        }
        findPreference<Preference>(getString(R.string.set_key_other_icon_selection))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                iconSelect()
                true
            }
        }
        findPreference<SwitchPreferenceCompat>(getString(R.string.set_key_other_mark))?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                "ICON_MARK_CHANGED_UPDATE".sendBroadcast(Bundle().apply {
                    putString("TYPE", "MARK")
                    putBoolean("MARK_STATE", newValue == true)
                })
                true
            }
        }
        findPreference<Preference>(getString(R.string.set_key_other_mark_edit))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                addMark()
                true
            }
        }
        findPreference<Preference>(getString(R.string.set_key_other_mark_color))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                selectMarkColor()
                true
            }
        }
    }

    private fun iconSelect() {
        activity?.let {
            IconSelect.get(it).apply {
                IconSelect.show()
            }
        }
    }

    private fun addMark(){
        context?.let {
            AlertDialog.Builder(it).apply {
                val layout = LayoutInflater.from(context).inflate(
                    R.layout.dialog_input,
                    null,
                    false
                )

                val edit = layout.findViewById<EditText>(R.id.edit_query)

                setView(layout)
                setPositiveButton("应用"){
                        _, _ ->
                    val mark = edit.text.toString()
                    "ICON_MARK_CHANGED_UPDATE".sendBroadcast(Bundle().apply {
                        putString("TYPE", "MARK")
                        putBoolean("MARK_STATE", true)
                        putString("MARK_TEXT", mark)
                    })
                }
                setNeutralButton("取消", null)
                setTitle(R.string.set_other_mark_edit)
                setCancelable(false)
                show()
            }
        }
    }

    private fun selectMarkColor(){
        ColorPickerDialogBuilder.with(context).apply {
            setTitle("字体颜色")
            initialColor(0xff000000.toInt())
            wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            density(12)
            setOnColorSelectedListener {

            }
            setPositiveButton("应用") { dialog, selectedColor, allColors ->
                "ICON_MARK_CHANGED_UPDATE".sendBroadcast(Bundle().apply {
                    putString("TYPE", "MARK")
                    putBoolean("MARK_STATE", true)
                    putInt("MARK_COLOR", selectedColor)
                })
            }
            setNegativeButton("取消", null)
            build().show()
        }
    }
}