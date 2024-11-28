package vn.mobile.clothing.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

import vn.mobile.clothing.fragments.OnboardingFragment

class OnboardingAdapter(activity: FragmentActivity, private val pagerList: ArrayList<Int>) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return pagerList.size
    }

    override fun createFragment(position: Int): Fragment {
        return OnboardingFragment(pagerList[position])

    }
}
