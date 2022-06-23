package cn.guo.onetext.adapter

import android.content.Intent
import android.os.Bundle
import cn.guo.onetext.R
import cn.guo.onetext.databinding.FontRecyclerItemBinding
import cn.guo.onetext.instance.FontSelect

class FontAdapter : BaseRecyclerAdapter<String>(R.layout.font_recycler_item) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val font = getItem(position)
        val itemBinding = FontRecyclerItemBinding.bind(holder.view)
        itemBinding.fontName.apply {
            setOnClickListener {
                FontSelect.dismiss()
                context.sendBroadcast(
                    Intent(
                        "${context.packageName}.FONT_RESOURCE_FILE"
                    ).putExtras(
                        Bundle().apply {
                            putInt("ID", position)
                        }
                    )
                )
            }
            text = font
        }
    }
}