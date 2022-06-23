package cn.guo.onetext.activity

import android.animation.Animator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cn.guo.onetext.PermissionMethods
import cn.guo.onetext.PictureManager
import cn.guo.onetext.R
import cn.guo.onetext.adapter.ViewPagerAdapter
import cn.guo.onetext.databinding.ActivityMainBinding
import cn.guo.onetext.widget.clipimage.Const
import cn.guo.onetext.dialog.BaseBottomSheet
import cn.guo.onetext.fragment.*
import cn.guo.onetext.widget.badgetextview.MenuItemBadge
import cn.guo.onetext.widget.slidinguppanel.SlidingUpPanelLayout
import com.google.android.material.tabs.TabLayout
import java.io.File

/**
 * @解决兼容性
 */
class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

    private lateinit var finishDialog:BaseBottomSheet
    private lateinit var menu:Menu
    private lateinit var selectPicture:PictureManager

    private val configUpdate=object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            replaceFragment(CardStyle())
            binding.contentLayout.mainSlidingUp.panelState=SlidingUpPanelLayout.PanelState.COLLAPSED
        }
    }

    private val saveImageFile = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            historyChanged()
        }
    }

    private val selectPictureReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!PermissionMethods.checkPermission(this@MainActivity, PermissionMethods.PERMISSIONS_STORAGE)) {
                PermissionMethods.storagePermissionDialog(this@MainActivity)
                return
            }
            val width= intent?.getIntExtra("WIDTH",0)!!
            val height= intent?.getIntExtra("HEIGHT",0)!!
            val isCrop = intent?.getBooleanExtra("IS_CROP",true)!!
            selectPicture.setNeedCrop(isCrop)
            selectPicture.setAspect(width, height)
            selectPicture.choosePhoto()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initBinding(ActivityMainBinding.inflate(layoutInflater))
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)

        binding.splashBottom.post {
            startSplashAnimator()
        }

        initViewPager()
        replaceFragment(CardStyle())
        initFinishDialog()
        configUpdate.registerReceiver("CARD_CHANGED_UPDATE")
        saveImageFile.registerReceiver("IMAGE_SAVED")
        selectPictureReceiver.registerReceiver("SELECT_PICTURE")
        selectPicture = PictureManager(this)
        selectPicture.setPictureSelectListener(object : PictureManager.PictureSelectListener {
            override fun onPictureSelect(imagePath: String?) {
                ClipImageActivity.Clip.prepare()
                    .aspectX(selectPicture.getAspectX())
                    .aspectY(selectPicture.getAspectY())
                    .inputPath(imagePath).outputPath(File(externalCacheDir, "crop.png").absolutePath)
                    .startForResult(this@MainActivity, Const.REQUEST_CLIP_IMAGE)
            }

            override fun throwError(e: Exception?) {
                e?.printStackTrace()
            }
        })
    }

    private fun initViewPager(){
        val windowHeight = getWindowSize( false)/2
        val viewAdapter = ViewPagerAdapter(supportFragmentManager, 1)
        viewAdapter.apply {
            addFragment(SetSave(), getString(R.string.set_save))
            addFragment(SetCard(), getString(R.string.set_card))
            addFragment(SetText(), getString(R.string.set_text))
            addFragment(SetOther(), getString(R.string.set_other))
            addFragment(SetHelp(), getString(R.string.set_help))
            //addFragment(SetApp(), getString(R.string.set_mini_app))
        }

        binding.contentLayout.viewPager.apply {
            adapter=viewAdapter
            currentItem = 1
            offscreenPageLimit=viewAdapter.count
            setOnTouchListener { v, event ->
                when (event?.action) {
                    MotionEvent.ACTION_MOVE -> binding.contentLayout.mainSlidingUp .isTouchEnabled = false
                    MotionEvent.ACTION_UP -> binding.contentLayout.mainSlidingUp.isTouchEnabled = true
                }
                true
            }
        }

        val constraintLayout = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomToBottom=0
            matchConstraintMaxHeight=windowHeight
        }
        (binding.contentLayout.viewPager.parent as LinearLayout).apply {
            layoutParams= constraintLayout
        }
        binding.contentLayout.tab.apply {
            setupWithViewPager(binding.contentLayout.viewPager)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (binding.contentLayout.mainSlidingUp.panelState != SlidingUpPanelLayout.PanelState.EXPANDED) {
                        changeSlidingState()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    return
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    changeSlidingState()
                }
            })
        }
        viewAdapter.getTitles().forEachIndexed { index, s ->
            binding.contentLayout.tab.getTabAt(index)?.text = s
        }

        binding.contentLayout.mainSlidingUp.apply {
            panelHeight=getActionBarHeight().toInt()
            setFadeOnClickListener {
                if(binding.contentLayout.mainSlidingUp.panelState!=SlidingUpPanelLayout.PanelState.EXPANDED)
                    binding.contentLayout.mainSlidingUp.panelState=SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }
    }

    private fun initFinishDialog(){
        finishDialog = BaseBottomSheet(this)
        finishDialog.apply {
            setTitle("提示")
            setMessage("请确保数据已保存再退出！")
            setPositiveButton("退出") { finish() }
        }
    }

    private fun changeSlidingState(){
        when(binding.contentLayout.mainSlidingUp.panelState){
            SlidingUpPanelLayout.PanelState.EXPANDED -> binding.contentLayout.mainSlidingUp.panelState =
                SlidingUpPanelLayout.PanelState.COLLAPSED
            SlidingUpPanelLayout.PanelState.COLLAPSED -> binding.contentLayout.mainSlidingUp.panelState =
                SlidingUpPanelLayout.PanelState.EXPANDED
        }
    }

    private fun startSplashAnimator(){
        val animationDuration:Long = 950
        ViewAnimationUtils.createCircularReveal(
            binding.splashBottom,
            binding.splashBottom.measuredWidth,
            binding.splashBottom.measuredHeight,
            binding.splashBottom.measuredHeight.toFloat(),
            0f
        ).setDuration(animationDuration).start()

        ViewAnimationUtils.createCircularReveal(
            binding.splashTop,
            0,
            0,
            binding.splashTop.measuredHeight.toFloat(),
            0f
        ).apply {
            duration = animationDuration
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    binding.splashTop.visibility = View.GONE
                    binding.splashBottom.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            start()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_card_root, fragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu=menu
        menuInflater.inflate(R.menu.menu_main, menu)
        val clazz = Class.forName("androidx.appcompat.view.menu.MenuBuilder")
        val method = clazz.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java)
        method.isAccessible = true
        method.invoke(menu, true)

        val menuItemNewFeature = menu.findItem(R.id.action_history)
        MenuItemBadge.update(
            this, menuItemNewFeature, MenuItemBadge.Builder()
                .iconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart))
                .textBackgroundColor(ContextCompat.getColor(this,R.color.colorBlue))
                .textColor(ContextCompat.getColor(this,R.color.white))
        )
        MenuItemBadge
            .getBadgeTextView(menuItemNewFeature)
            .setBadgeCount(getImageNumber())
        return true
    }

    private fun historyChanged(){
        if(this::menu.isInitialized)
        MenuItemBadge
            .getBadgeTextView(menu.findItem(R.id.action_history))
            .setBadgeCount(getImageNumber())
    }

    private fun getImageNumber():Int{
        return getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.list().size
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: ")
         when (item.itemId) {
             R.id.action_history -> activityTo(HistoryActivity::class)
             R.id.action_about -> activityTo(AboutActivity::class)
             R.id.action_settings -> activityTo(SettingsActivity::class)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode){
            KeyEvent.KEYCODE_BACK -> {
                if (binding.contentLayout.mainSlidingUp.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    binding.contentLayout.mainSlidingUp.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                } else {
                    finishDialog.show()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        historyChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        selectPicture.onActivityResult(requestCode, resultCode, data)
    }
}