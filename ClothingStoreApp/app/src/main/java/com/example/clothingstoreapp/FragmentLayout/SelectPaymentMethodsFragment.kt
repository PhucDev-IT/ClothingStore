package com.example.clothingstoreapp.FragmentLayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.FragmentSelectPaymentMethodsBinding

class SelectPaymentMethodsFragment : Fragment() {

    private lateinit var _binding:FragmentSelectPaymentMethodsBinding
    private val binding get() = _binding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectPaymentMethodsBinding.inflate(inflater,container,false)
        handleOnClick()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    private fun handleOnClick(){
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnApply.setOnClickListener {
            requireActivity().onBackPressed()
        }


    }
}