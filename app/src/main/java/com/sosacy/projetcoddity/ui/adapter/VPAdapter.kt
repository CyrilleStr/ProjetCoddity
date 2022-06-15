package com.sosacy.projetcoddity.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class VPAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val mFragmentArrayList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItemCount(): Int {
        return mFragmentArrayList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentArrayList[position]
    }

    fun addFragment(fragment: Fragment, title: String){
        mFragmentArrayList.add(fragment)
        mFragmentTitleList.add(title)
    }
}