package com.example.clothingstoreapp.FragmentLayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.RvMyAddressAdapter
import com.example.clothingstoreapp.Adapter.RvOptionsAddressAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.AddressModel
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.AddressService
import com.example.clothingstoreapp.ViewModel.PayOrderViewModel
import com.example.clothingstoreapp.databinding.FragmentMyAddressBinding
import com.example.clothingstoreapp.databinding.FragmentOptionAddressBinding


class OptionAddressFragment : Fragment() {
    private lateinit var _binding: FragmentOptionAddressBinding
    private val binding get() = _binding
    private lateinit var navController: NavController
    private val sharedViewModel: PayOrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOptionAddressBinding.inflate(inflater,container,false)

        initView()
        handleClick()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    private fun initView(){
        val list = UserManager.getInstance().getUserCurrent()?.listAddress

        val adapter = list?.let {
            RvOptionsAddressAdapter(it,object : ClickObjectInterface<AddressModel> {
                override fun onClickListener(t: AddressModel) {
                   sharedViewModel.setDeliveryAddress(t)
                }
            })
        }

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)

        binding.rvAddress.adapter = adapter
        binding.rvAddress.layoutManager = linearLayoutManager


    }

    private fun handleClick(){
        binding.lnAddNewAddress.setOnClickListener {
            navController.navigate(R.id.action_myAddressFragment_to_add_AddressFragment)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnConfirm.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}