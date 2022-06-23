package cn.guo.onetext.instance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import cn.guo.onetext.R
import cn.guo.onetext.adapter.FontAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialog

class FontSelect private constructor() {

    companion object {

        private lateinit var dialog: BottomSheetDialog
        private lateinit var context: Context

        private val themeChanged = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                instance = null
            }
        }

        private var instance: FontSelect? = null
            get() {
                if (field == null) {
                    field = FontSelect()
                    init()
                    context.registerReceiver(themeChanged,
                        IntentFilter("${context.packageName}.THEME_CHANGED"))
                }
                return field
            }

        @Synchronized
        fun get(context: Context?): FontSelect {
            if (context != null) Companion.context = context
            return instance!!
        }

        private fun init() {
            val view = LayoutInflater.from(context).inflate(
                R.layout.font_recycler_root,
                null,
                false
            )

            val list = getFont(ArrayList<String>())

            val manager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
            }
            (view.findViewById(R.id.font_recycler) as RecyclerView).apply {
                layoutManager = manager
                adapter = FontAdapter().apply {
                    setData(list)
                }
            }
            dialog = BottomSheetDialog(context, R.style.AlphaDialogStyle).apply {
                setContentView(view)
                create()
            }
        }

        private fun getFont(fonts: ArrayList<String>): ArrayList<String> {
            val fontsRes = intArrayOf(R.font.font_1,
                R.font.font_2,
                R.font.font_3,
                R.font.font_4,
                R.font.font_5,
                R.font.font_6,
                R.font.font_7,
                R.font.font_8,
                R.font.font_9,
                R.font.font_10)
            val fontsName = context.resources.getStringArray(R.array.font_list)
            fontsName.forEach {
                fonts.add(it)
            }
            return fonts
        }

        @Synchronized
        fun show() {
            if (!this::dialog.isInitialized) return
            dialog.show()
        }

        @Synchronized
        fun dismiss() {
            if (!this::dialog.isInitialized) return
            dialog.dismiss()
        }
    }

}