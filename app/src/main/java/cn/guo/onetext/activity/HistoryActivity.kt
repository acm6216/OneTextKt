package cn.guo.onetext.activity

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.guo.onetext.R
import cn.guo.onetext.Utils
import cn.guo.onetext.adapter.BaseRecyclerAdapter
import cn.guo.onetext.alerter.Alerter
import cn.guo.onetext.databinding.ActivityHistoryBinding
import cn.guo.onetext.databinding.RecyclerPictureItemBinding
import java.io.File

/**
 * @解决兼容性
 */
class HistoryActivity : BaseBindingActivity<ActivityHistoryBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initBinding(ActivityHistoryBinding.inflate(layoutInflater))
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadPicture()
    }

    private fun loadPicture() {
        val pics = ArrayList<PictureItem>()
        getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.list { dir, name ->
            pics.add(PictureItem(dir.absolutePath, name))
        }
        val pictureAdapter = BaseRecyclerAdapter<PictureItem>(R.layout.recycler_picture_item) {
            onBindViewHolder { holder, position ->
                val item = getItem(position)
                val filePath = item.dir + File.separator + item.name
                val bitmap = BitmapFactory.decodeFile(item.dir + File.separator + item.name)
                val viewItem = RecyclerPictureItemBinding.bind(holder.itemView)

                viewItem.historyPictureRoot.setOnClickListener {
                    val popupMenu = PopupMenu(this@HistoryActivity, it)
                    popupMenu.apply {
                        menu.add("导出").setOnMenuItemClickListener {
                            val state = Utils.saveBitmapToPicture(
                                this@HistoryActivity, BitmapFactory.decodeFile(filePath), item.name)
                            Alerter.create(this@HistoryActivity)
                                .setText("导出${if (state) "成功" else "失败"}")
                                .setBackgroundColorRes(R.color.colorBlue)
                                .show()
                            true
                        }
                        menu.add("删除").setOnMenuItemClickListener {
                            val state = File(filePath).delete()
                            Alerter.create(this@HistoryActivity)
                                .setText("删除${if (state) "成功" else "失败"}")
                                .setBackgroundColorRes(R.color.colorBlue)
                                .show()
                            removeItem(position)
                            false
                        }
                    }.show()
                }
                viewItem.historyPictureTitle.text = item.name
                viewItem.historyPictureDisplay.setImageBitmap(bitmap)
            }
        }
        pictureAdapter.setData(pics)
        binding.historyRecycler.apply {
            adapter = pictureAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    private fun deleteAllPic() {
        AlertDialog.Builder(this).apply {
            setTitle("此操作会删除所有未导出的图片，是否继续？")
            setPositiveButton("删除") {
                    _, _ ->
                this@HistoryActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.list { dir, name ->
                        File(dir.absolutePath + File.separator + name).delete()
                    }
                loadPicture()
            }
            setNegativeButton("取消", null)
                .show()
        }
    }

    data class PictureItem(val dir: String, val name: String)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_delete -> deleteAllPic()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_histroy, menu)
        return super.onCreateOptionsMenu(menu)
    }
}