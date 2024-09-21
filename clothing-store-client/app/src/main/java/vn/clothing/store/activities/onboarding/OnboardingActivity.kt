package vn.clothing.store.activities.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import vn.clothing.store.MainActivity
import vn.clothing.store.R
import vn.clothing.store.adapter.OnboardingAdapter
import vn.clothing.store.adapter.ViewPagerOnBoarding
import vn.clothing.store.databinding.ActivityOnboardingBinding
import vn.clothing.store.utils.MySharedPreferences

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter

    private val onBoardingPageChangeCallback = object : ViewPager2.OnPageChangeCallback(){
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val images = arrayListOf(R.drawable.onboarding_1, R.drawable.onboarding_2)
        binding.viewPager.apply {
            adapter = OnboardingAdapter(this@OnboardingActivity,images)
            registerOnPageChangeCallback(onBoardingPageChangeCallback)
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
        binding.circleIndicator.setViewPager(binding.viewPager)
        initView()
    }

    private fun initView(){

        binding.btnNext.setOnClickListener {
            if(binding.viewPager.currentItem < binding.viewPager.adapter!!.itemCount -1){
                binding.viewPager.currentItem += 1
            }else{
                startWelcomeScreen()
            }
        }

        binding.btnPrevious.setOnClickListener {
            if(binding.viewPager.currentItem >0){
                binding.viewPager.currentItem -= 1
            }
        }
    }

    private fun startWelcomeScreen(){
        MySharedPreferences.setBooleanValue(this, MySharedPreferences.PREF_WAS_ONBOARDING,true)
        val intent = Intent(this, WelcomeScreenActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}