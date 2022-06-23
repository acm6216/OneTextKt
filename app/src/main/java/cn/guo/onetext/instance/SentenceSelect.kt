package cn.guo.onetext.instance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.guo.onetext.R
import cn.guo.onetext.Utils
import cn.guo.onetext.adapter.SentenceAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class SentenceSelect private constructor() {

        companion object {

            private const val TAG = "SentenceSelect"
            private lateinit var dialog: BottomSheetDialog
            private lateinit var context: Context

            private val themeChanged = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    instance = null
                }
            }

            private var instance: SentenceSelect? = null
                get() {
                    if (field == null) {
                        field = SentenceSelect()
                        init()
                        context.registerReceiver(themeChanged, IntentFilter("${context.packageName}.THEME_CHANGED"))
                    }
                    return field
                }

            @Synchronized
            fun get(context: Context?): SentenceSelect {
                if(context!=null) Companion.context = context
                return instance!!
            }

            private fun init(){
                val view = LayoutInflater.from(context).inflate(
                    R.layout.sentence_rectcler_root,
                    null,
                    false
                )
                val manager = LinearLayoutManager(context)
                (view.findViewById(R.id.sentence_recycler) as RecyclerView).apply {
                    layoutManager = manager
                    adapter = SentenceAdapter().apply {
                        setData(getSentence())
                    }
                }
                dialog = BottomSheetDialog(context, R.style.AlphaDialogStyle).apply {
                    setContentView(view)
                    create()
                }
            }

            private fun getSentence():ArrayList<String>{
                val result = ArrayList<String>()
                Utils.readAssetsTxt(context, "info.xml")?.split("\n")?.forEach {
                    if(it!="") result.add(it)
                }
                return  result
            }

            @Synchronized
            fun show(){
                if(!this::dialog.isInitialized) return
                dialog.show()
            }

            @Synchronized
            fun dismiss(){
                if(!this::dialog.isInitialized) return
                dialog.dismiss()
            }
        }

    }