package cn.guo.onetext.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

class SuperViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    public val STATE_DOWN = MotionEvent.ACTION_DOWN
    public val STATE_UP = MotionEvent.ACTION_UP
    public val STATE_MOVE = MotionEvent.ACTION_MOVE

    private val state = STATE_UP
    private var mOnTouchListener: View.OnTouchListener?=null

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mOnTouchListener != null) mOnTouchListener!!.onTouch(this,ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun setOnTouchListener(l:View.OnTouchListener) {
        mOnTouchListener = l
    }

}