package cn.guo.onetext.activity

import android.os.Bundle
import cn.guo.onetext.databinding.ActivityDoodleBinding

/**
 * @解决兼容性
 */
class DoodleActivity: BaseBindingActivity<ActivityDoodleBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initBinding(ActivityDoodleBinding.inflate(layoutInflater))
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.hide()
    }
}