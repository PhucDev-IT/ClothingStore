package com.example.clothingstoreadmin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.clothingstoreadmin.FragmentLayout.WaitingForDeliveryFragment
import com.example.clothingstoreadmin.FragmentLayout.ShippingOrderFragment
import com.example.clothingstoreadmin.FragmentLayout.WaitingConfirmFragment


class ViewPagerOderAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> WaitingConfirmFragment()
            1-> WaitingForDeliveryFragment()
            2 -> ShippingOrderFragment()


            else -> WaitingConfirmFragment()
        }
    }
}