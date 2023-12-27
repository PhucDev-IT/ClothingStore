package com.example.clothingstoreapp.FragmentLayout

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.FragmentVerifyCodeBinding


class VerifyCodeFragment : Fragment() {

    private lateinit var _binding: FragmentVerifyCodeBinding
    private val binding get() = _binding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyCodeBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }


    //Set up input OTP
    private fun setUpOTPInputs() {
        binding.digit1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    binding.digit2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // không cần thực hiện
            }
        })
        binding.digit2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    binding.digit3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // không cần thực hiện
            }
        })
        binding.digit3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    binding.digit4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // không cần thực hiện
            }
        })
        binding.digit4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    binding.digit5.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // không cần thực hiện
            }
        })
        binding.digit5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    binding.digit6.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // không cần thực hiện
            }
        })
    }
}