package com.example.clothingstoreapp.FragmentLayout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clothingstoreapp.Activity.LoginScreen
import com.example.clothingstoreapp.Activity.SignUpScreen
import com.example.clothingstoreapp.Data_Local.DataLocalManager
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.FragmentWelcomeScreenBinding


class WelcomeScreenFragment : Fragment() {

    private lateinit var _binding:FragmentWelcomeScreenBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeScreenBinding.inflate(inflater,container,false)


        binding.btnLetsStart.setOnClickListener {
            DataLocalManager.setUsedApplication()
            val intent = Intent(context,LoginScreen::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
        }

        binding.tvSignIn.setOnClickListener {
            val intent = Intent(context,SignUpScreen::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
        }

        return binding.root
    }
}