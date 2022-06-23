package cn.guo.onetext.activity

import android.os.Bundle
import cn.guo.onetext.databinding.ActivityTestBinding

/**
 * @解决兼容性
 */
class TestActivity : BActivity<ActivityTestBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initBinding(ActivityTestBinding.inflate(layoutInflater))
        super.onCreate(savedInstanceState)


    }
}