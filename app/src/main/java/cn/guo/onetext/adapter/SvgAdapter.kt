package cn.guo.onetext.adapter

import android.content.Intent
import android.os.Bundle
import cn.guo.onetext.R
import cn.guo.onetext.databinding.SvgRecyclerItemBinding
import cn.guo.onetext.instance.IconSelect
import cn.guo.onetext.instance.IconSelect.Companion.dismiss

class SvgAdapter : BaseRecyclerAdapter<String>(R.layout.svg_recycler_item) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val path = getItem(position)
        SvgRecyclerItemBinding.bind(holder.view).apply {
            svgItem!!.apply {
                startSvgDataResource(path)
                setViewportSize(1024f, 1024f)
                setTraceResidueColor(0x22000000)
                setTraceTime(0)
                setFillTime(0)
                rebuildGlyphData()
                start()
            }

            svgRoot.setOnClickListener {
                IconSelect.get(null).apply {
                    dismiss()
                }
                context.sendBroadcast(
                    Intent(
                        "${context.packageName}.ICON_MARK_CHANGED_UPDATE"
                    ).putExtras(
                        Bundle().apply {
                            putString("TYPE", "ICON")
                            putBoolean("ICON_STATE", true)
                            putString("ICON_NAME", path)
                        }
                    )
                )
            }
        }
    }
}