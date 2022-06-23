package cn.guo.onetext.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import cn.guo.onetext.R
import cn.guo.onetext.adapter.OpenSourceDataAdapter
import cn.guo.onetext.databinding.ActivityOpenSourceBinding

/**
 * @解决兼容性
 */
class OpenSourceActivity : BaseBindingActivity<ActivityOpenSourceBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        initBinding(ActivityOpenSourceBinding.inflate(layoutInflater))
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadData(){
        val dataArray = mutableListOf<OpenSourceDataAdapter.OpenSource>()
        val titleArray = resources.getStringArray(R.array.open_source_title)
        val infoArray = resources.getStringArray(R.array.open_source_info)
        val urlArray = resources.getStringArray(R.array.open_source_url)
        titleArray.forEachIndexed { index, s ->
            if(s != "") {
                val openSource = OpenSourceDataAdapter.OpenSource(s, infoArray[index], urlArray[index])
                dataArray.add(openSource)
            }
        }

        binding.recyclerOpenSource.apply {
            layoutManager = LinearLayoutManager(this@OpenSourceActivity, LinearLayoutManager.VERTICAL, false)
            adapter = OpenSourceDataAdapter().apply {
                setData(dataArray)
            }
        }
    }
}