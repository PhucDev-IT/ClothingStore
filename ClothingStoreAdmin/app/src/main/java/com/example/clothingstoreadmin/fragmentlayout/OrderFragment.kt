package com.example.clothingstoreadmin.fragmentlayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.clothingstoreadmin.adapter.ViewPagerOderAdapter
import com.example.clothingstoreadmin.databinding.FragmentOrderBinding
import com.google.android.material.tabs.TabLayout


class OrderFragment : Fragment() {
   private lateinit var _binding:FragmentOrderBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater,container,false)
        initView()

        return binding.root
    }

    private fun initView(){
        val viewPagerPurchasedAdapter: ViewPagerOderAdapter = ViewPagerOderAdapter(requireActivity())
        binding.viewPager2.adapter = viewPagerPurchasedAdapter

        binding.tabLayout.addOnTabSelectedListener(object  : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager2.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)?.select()
            }
        })
    }
}