package com.example.clothingstoreadmin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.clothingstoreadmin.fragmentlayout.OnBoardingOneFragment
import com.example.clothingstoreadmin.fragmentlayout.OnBoardingTwoFragment
import com.example.clothingstoreadmin.fragmentlayout.WelcomeScreenFragment


class ViewPagerOnBoarding(frm:FragmentManager, behavior: Int): FragmentStatePagerAdapter(frm,behavior) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> OnBoardingOneFragment()
            1 -> OnBoardingTwoFragment()
            2 -> WelcomeScreenFragment()
            else-> OnBoardingOneFragment()
        }
    }
}