package vn.clothing.store.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.clothing.store.R
import vn.clothing.store.activities.onboarding.WelcomeScreenActivity
import vn.clothing.store.databinding.FragmentOnboardingBinding


class OnboardingFragment(private val tab:Int) : Fragment() {
    private lateinit var _binding: FragmentOnboardingBinding
    private val binding get() =  _binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(inflater,container,false)
        initUi()
        return binding.root
    }

    private fun initUi(){
        binding.image.setImageResource(R.drawable.onboarding_1)
    }
}