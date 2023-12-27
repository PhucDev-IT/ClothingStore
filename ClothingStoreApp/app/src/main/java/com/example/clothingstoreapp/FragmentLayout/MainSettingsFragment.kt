package com.example.clothingstoreapp.FragmentLayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.FragmentMainSettingsBinding


class MainSettingsFragment : Fragment() {
    private lateinit var _binding:FragmentMainSettingsBinding
    private val binding get() = _binding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainSettingsBinding.inflate(inflater,container,false)
        handleClick()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }


    private fun handleClick(){
        binding.lnAddress.setOnClickListener {
            navController.navigate(R.id.action_mainSettingsFragment_to_myAddressFragment)
        }
    }
}