package cn.guo.onetext.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

open class BaseRecyclerAdapter<K>(
    @LayoutRes val layoutId:Int,
    val list: Collection<K>? = null,
    bind: (BaseRecyclerAdapter<K>.() -> Unit)? = null,
) : RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder>() {

    init {
        if (bind != null) {
            apply(bind)
        }
    }

    private val TAG = javaClass.simpleName

    private var onBindViewHolder: ((holder: ViewHolder, position: Int) -> Unit)? = null
    lateinit var context: Context
    private var dataList = mutableListOf<K>()

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val view = v
    }

    fun onBindViewHolder(onBindViewHolder: ((holder: ViewHolder, position: Int) -> Unit)) {
        this.onBindViewHolder = onBindViewHolder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (onBindViewHolder != null) {
            onBindViewHolder!!.invoke(holder, position)
        } else {
            bindData(holder, position)
        }
    }

    fun removeItem(position:Int){
        dataList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,dataList.size)
    }

    override fun getItemCount(): Int = dataList?.size ?: 0

    override fun getItemViewType(position: Int): Int =position

    private fun bindData(holder: ViewHolder, position: Int) {

    }

    fun setData(list: Collection<K>?): Boolean {
        var result = false
        dataList.clear()
        if (list != null) {
            result = dataList.addAll(list)
        }
        return result
    }

    fun getItem(position: Int) = dataList[position]

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

}