package com.example.clothingstoreapp.Adapter

import androidx.coordinatorlayout.widget.CoordinatorLayout.AttachedBehavior
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.clothingstoreapp.FragmentLayout.OnBoardingOneFragment
import com.example.clothingstoreapp.FragmentLayout.OnBoardingTwoFragment
import com.example.clothingstoreapp.FragmentLayout.WelcomeScreenFragment

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