package com.example.clothingstoreapp.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.clothingstoreapp.FragmentLayout.DangDatHangFragment
import com.example.clothingstoreapp.FragmentLayout.DonHangDaHuyFragment
import com.example.clothingstoreapp.FragmentLayout.LichSuMuaHangFragment

class ViewPagerPurchasedAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> DangDatHangFragment()
            1-> LichSuMuaHangFragment()
            2-> DonHangDaHuyFragment()
            else -> LichSuMuaHangFragment()
        }
    }
}