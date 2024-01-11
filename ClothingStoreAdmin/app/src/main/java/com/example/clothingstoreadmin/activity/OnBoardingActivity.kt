package com.example.clothingstoreadmin.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.adapter.ViewPagerOnBoarding
import com.example.clothingstoreadmin.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var viewPagerAdapter: ViewPagerOnBoarding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = ViewPagerOnBoarding(supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        binding.viewPager.adapter = viewPagerAdapter

        binding.circleIndicator.setViewPager(binding.viewPager)

        initView()
    }

    private fun initView(){
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                binding.tvSkip.visibility = View.VISIBLE
                if(position == 0){
                    binding.btnPrevious.visibility = View.INVISIBLE
                    binding.tvTitle.text = resources.getString(R.string.onboarding_1)
                }else if(position == 1){
                    binding.tvTitle.text = resources.getString(R.string.onboarding_2)
                    binding.btnPrevious.visibility = View.VISIBLE
                }else{
                    binding.tvSkip.visibility = View.GONE
                    binding.lnControlBottom.visibility = View.GONE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        binding.tvSkip.setOnClickListener {
            binding.viewPager.currentItem = 2
        }

        binding.btnNext.setOnClickListener {
            if(binding.viewPager.currentItem <2){
                binding.viewPager.currentItem += 1
            }
        }

        binding.btnPrevious.setOnClickListener {
            if(binding.viewPager.currentItem >0){
                binding.viewPager.currentItem -= 1
            }
        }
    }
}