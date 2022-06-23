package cn.guo.onetext.adapter

import android.graphics.Paint
import androidx.appcompat.widget.PopupMenu
import cn.guo.onetext.R
import cn.guo.onetext.activity.BaseActivity
import cn.guo.onetext.databinding.RecylerOpenSourceItemBinding

class OpenSourceDataAdapter :
    BaseRecyclerAdapter<OpenSourceDataAdapter.OpenSource>(R.layout.recyler_open_source_item) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val openSource = getItem(position)
        RecylerOpenSourceItemBinding.bind(holder.view).apply {
            openSourceTitle.text = openSource.title
            openSourceUri.apply {
                paint.flags = Paint.UNDERLINE_TEXT_FLAG
                paint.isAntiAlias = true
                text=openSource.url
            }
            openSourceLicense.text = openSource.license
            openSourceItemRoot.setOnClickListener {
                val menu = PopupMenu(context, it)
                menu.menu.apply {
                    add("复制链接").setOnMenuItemClickListener {
                        (context as BaseActivity).apply {
                            "已复制".toast()
                            setCopyText(context, openSource.url)
                        }
                        false
                    }
                    add("打开链接").setOnMenuItemClickListener {
                        (context as BaseActivity).apply {
                            openUrl(openSource.url)
                        }
                        false
                    }
                }
                menu.show()
            }
        }
    }

    data class OpenSource(var title: String, var license: String, var url: String)
}