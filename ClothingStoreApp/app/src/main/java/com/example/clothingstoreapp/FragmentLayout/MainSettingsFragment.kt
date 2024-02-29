package com.example.clothingstoreapp.FragmentLayout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.clothingstoreapp.Activity.LoginScreen
import com.example.clothingstoreapp.Adapter.CustomDialog
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.FragmentMainSettingsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


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

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnLogout.setOnClickListener {
           val customDialog = context?.let { it1 -> CustomDialog(it1) }
            customDialog?.dialogBasic(
                "ĐĂNG XUẤT",
                "Bạn có chắc chắc muốn đăng xuất khỏi ứng dụng?"
            ) { b ->
                if (b) {
                    Firebase.auth.signOut()
                    val intent = Intent(context, LoginScreen::class.java)
                    requireActivity().finish()
                    startActivity(intent)
                }
            }
        }


    }
}