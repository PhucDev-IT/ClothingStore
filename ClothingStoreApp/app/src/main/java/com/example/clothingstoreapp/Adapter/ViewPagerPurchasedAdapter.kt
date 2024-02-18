package com.example.clothingstoreapp.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.clothingstoreapp.FragmentLayout.DangDatHangFragment
import com.example.clothingstoreapp.FragmentLayout.DonHangDaHuyFragment
import com.example.clothingstoreapp.FragmentLayout.LichSuMuaHangFragment
import com.example.clothingstoreapp.FragmentLayout.PreviewInPurchaseFragment
import com.example.clothingstoreapp.FragmentLayout.WaitingConfirmOrderFragment

class ViewPagerPurchasedAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WaitingConfirmOrderFragment()
            1 -> LichSuMuaHangFragment()
            2 -> DonHangDaHuyFragment()
            3 -> PreviewInPurchaseFragment()
            else -> LichSuMuaHangFragment()
        }
    }
}