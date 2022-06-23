package cn.guo.onetext.instance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import cn.guo.onetext.R
import cn.guo.onetext.adapter.SvgAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialog

class IconSelect private constructor() {

    companion object {

        private lateinit var dialog: BottomSheetDialog
        private lateinit var context: Context

        private val themeChanged = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                instance = null
            }
        }

        private var instance: IconSelect? = null
            get() {
                if (field == null) {
                    field = IconSelect()
                    init()
                    context.registerReceiver(themeChanged, IntentFilter("${context.packageName}.THEME_CHANGED"))
                }
                return field
            }

        @Synchronized
        fun get(context: Context?): IconSelect {
            if (context != null) Companion.context = context
            return instance!!
        }

        private fun init() {
            val view = LayoutInflater.from(context).inflate(
                R.layout.svg_recycler_root,
                null,
                false
            )
            val list = getSvg(ArrayList<String>(), "svg/")
            val manager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
            }
            (view.findViewById(R.id.svg_recycler) as RecyclerView).apply {
                layoutManager = manager
                adapter = SvgAdapter().apply {
                    setData(list)
                }
            }
            dialog = BottomSheetDialog(context, R.style.AlphaDialogStyle).apply {
                setContentView(view)
                create()
            }
        }

        private fun getSvg(svg: ArrayList<String>, path: String): ArrayList<String> {
            val list = context.assets.list(path)
            list?.forEach {
                if (it.toLowerCase().contains(".svg")) {
                    svg.add(path + it)
                } else {
                    getSvg(svg, "${path}${it}/")
                }
            }
            return svg;
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