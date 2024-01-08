package com.example.clothingstoreapp.Activity

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.clothingstoreapp.Adapter.ViewPagerPurchasedAdapter
import com.example.clothingstoreapp.databinding.ActivityPurchasedHistoryScreenBinding
import com.google.android.material.tabs.TabLayout


class PurchasedHistoryScreen : AppCompatActivity() {

    private lateinit var binding: ActivityPurchasedHistoryScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasedHistoryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPagerPurchasedAdapter: ViewPagerPurchasedAdapter = ViewPagerPurchasedAdapter(this)
        binding.viewPager2.adapter = viewPagerPurchasedAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)?.select()
            }
        })

        // Mario Velasco's code
        // Mario Velasco's code
        binding.tabLayout.post {
            val tabLayoutWidth: Int = binding.tabLayout.width
            val metrics = DisplayMetrics()
            this@PurchasedHistoryScreen.windowManager.getDefaultDisplay().getMetrics(metrics)
            val deviceWidth = metrics.widthPixels
            if (tabLayoutWidth < deviceWidth) {
                binding.tabLayout.tabMode = TabLayout.MODE_FIXED
                binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            } else {
                binding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            }
        }


        initUI()

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun initUI() {
        val tab = intent.getStringExtra("key_tab")

        when (tab) {
            "Processing" -> binding.viewPager2.currentItem = 0
            "Purchase" -> binding.viewPager2.currentItem = 1
            "isPreview" -> binding.viewPager2.currentItem = 3
            else -> binding.viewPager2.currentItem = 0
        }
    }

}