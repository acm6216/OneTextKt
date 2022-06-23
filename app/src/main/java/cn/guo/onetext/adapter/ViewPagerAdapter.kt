package cn.guo.onetext.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlin.collections.ArrayList

class ViewPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {

    private val mFragmentList: ArrayList<Fragment> = ArrayList()
    private val mFragmentsTitles: ArrayList<String> = ArrayList()

    fun addFragment(fragment: Fragment?, fragmentTitle: String?) {
        fragment?.let { mFragmentList.add(it) }
        fragmentTitle?.let { mFragmentsTitles.add(it) }
    }

    fun getTitle(position: Int): String? = mFragmentsTitles[position]

    fun getTitles():ArrayList<String> = mFragmentsTitles

    override fun getCount(): Int =mFragmentList.size

    override fun getItem(position: Int): Fragment = mFragmentList[position]
}