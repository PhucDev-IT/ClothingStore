package com.example.clothingstoreapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.clothingstoreapp.Adapter.ViewPagerPurchasedAdapter
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ActivityPurchasedHistoryScreenBinding
import com.google.android.material.tabs.TabLayout

class PurchasedHistoryScreen : AppCompatActivity() {

    private lateinit var binding:ActivityPurchasedHistoryScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasedHistoryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPagerPurchasedAdapter: ViewPagerPurchasedAdapter = ViewPagerPurchasedAdapter(this)
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