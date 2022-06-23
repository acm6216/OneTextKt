package cn.guo.onetext.activity

import android.os.Bundle
import android.os.Handler
import cn.guo.onetext.R

/**
 * @解决兼容性
 */
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed(Runnable {
            activityTo(MainActivity::class)
            overridePendingTransition(R.anim.activity_in,R.anim.activity_exit2)
            finish()
        }, 500)
    }

}