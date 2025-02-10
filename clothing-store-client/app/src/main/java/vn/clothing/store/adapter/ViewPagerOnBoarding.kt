package vn.clothing.store.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import vn.clothing.store.fragments.OnboardingFragment


class ViewPagerOnBoarding(frm: FragmentManager, behavior: Int): FragmentStatePagerAdapter(frm,behavior) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> OnboardingFragment(0)
            1 -> OnboardingFragment(1)
            2 ->OnboardingFragment(2)
            else-> OnboardingFragment(0)
        }
    }
}