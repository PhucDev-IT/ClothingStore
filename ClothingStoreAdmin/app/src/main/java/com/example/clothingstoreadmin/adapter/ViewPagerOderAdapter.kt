package com.example.clothingstoreadmin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.clothingstoreadmin.fragmentlayout.ChoGiaoHangFragment
import com.example.clothingstoreadmin.fragmentlayout.DangGiaoHangFragment


class ViewPagerOderAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ChoGiaoHangFragment()
            1-> DangGiaoHangFragment()

            else -> ChoGiaoHangFragment()
        }
    }
}