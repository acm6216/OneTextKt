package cn.guo.onetext.adapter

import android.content.Intent
import android.os.Bundle
import cn.guo.onetext.R
import cn.guo.onetext.databinding.SentenceRecyclerItemBinding
import cn.guo.onetext.instance.SentenceSelect

class SentenceAdapter : BaseRecyclerAdapter<String>(R.layout.sentence_recycler_item) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val sentence = getItem(position)
        SentenceRecyclerItemBinding.bind(holder.view).sentenceItem.apply {
            setOnClickListener {
                SentenceSelect.dismiss()
                context.sendBroadcast(
                    Intent(
                        "${context.packageName}.TEXT_CHANGED_FOR_SENTENCE"
                    ).putExtras(
                        Bundle().apply {
                            putString("SENTENCE", sentence)
                        }
                    )
                )
            }
            text = sentence
        }
    }
}