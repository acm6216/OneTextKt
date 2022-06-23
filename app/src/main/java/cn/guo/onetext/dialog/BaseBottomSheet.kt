package cn.guo.onetext.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import cn.guo.onetext.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class BaseBottomSheet(private val context: Context) {

    private var dialog: BottomSheetDialog? = null
    private lateinit var root: View
    private var title: TextView? = null
    private var message: TextView? = null
    private var close: ImageView? = null
    private var positive: Button? = null

    private fun build() {
        dialog = BottomSheetDialog(context, R.style.AlphaDialogStyle)
        root = LayoutInflater.from(context).inflate(R.layout.dialog_base_bottom_sheet, null, false)
        dialog!!.setContentView(root)
        findViews()
    }

    private fun findViews() {
        title = root!!.findViewById(R.id.base_bottom_sheet_title)
        message = root!!.findViewById(R.id.base_bottom_sheet_content)
        close = root!!.findViewById(R.id.base_bottom_sheet_close)
        positive = root!!.findViewById(R.id.base_bottom_sheet_positive_button)
        setListener()
    }

    private fun setListener() {
        close!!.setOnClickListener { dialog!!.cancel() }
    }

    fun setTitle(title: String?): BaseBottomSheet {
        this.title!!.text = title
        return this
    }

    fun setPositiveButton(text: String?, listener: View.OnClickListener?): BaseBottomSheet {
        positive!!.text = text
        positive!!.setOnClickListener(listener)
        return this
    }

    fun setMessage(msg: String?): BaseBottomSheet {
        message!!.text = msg
        return this
    }

    fun show() {
        dialog!!.show()
    }

    init {
        build()
    }
}