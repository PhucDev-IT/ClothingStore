package com.example.clothingstoreadmin.fragmentlayout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clothingstoreadmin.DataLocal.DataLocalManager
import com.example.clothingstoreadmin.activity.LoginScreen
import com.example.clothingstoreadmin.databinding.FragmentWelcomeScreenBinding



class WelcomeScreenFragment : Fragment() {

    private lateinit var _binding: FragmentWelcomeScreenBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeScreenBinding.inflate(inflater,container,false)


        binding.btnLetsStart.setOnClickListener {
            DataLocalManager.setUsedApplication()
            val intent = Intent(context, LoginScreen::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
        }


        return binding.root
    }
}