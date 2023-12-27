package com.example.clothingstoreapp.FragmentLayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.RvMyAddressAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.AddressModel
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.AddressService
import com.example.clothingstoreapp.databinding.FragmentAddAddressBinding
import com.example.clothingstoreapp.databinding.FragmentMyAddressBinding

class MyAddressFragment : Fragment() {

    private lateinit var _binding: FragmentMyAddressBinding
    private val binding get() = _binding
    private val addressService = AddressService()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyAddressBinding.inflate(inflater,container,false)

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
            RvMyAddressAdapter(it,object : ClickObjectInterface<AddressModel>{
                override fun onClickListener(t: AddressModel) {
                    Toast.makeText(context,"Bạn chọn: ${t.tinhThanhPho}",Toast.LENGTH_SHORT).show()
                }
            })
        }

        val linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        binding.rvAddress.adapter = adapter
        binding.rvAddress.layoutManager = linearLayoutManager


        val navController = findNavController()
        val navBackStackEntry = navController.previousBackStackEntry

        if (navBackStackEntry != null) {
            val destination = navBackStackEntry.destination // FragmentInfo của Fragment trước đó
            val fragmentId = destination.id // ID của Fragment trước đó

            Toast.makeText(context,"Previous: $fragmentId",Toast.LENGTH_SHORT).show()
            // Thực hiện các thao tác với thông tin của Fragment trước đó
            // Ví dụ: Log.d("PreviousFragment", "Previous Fragment ID: $fragmentId")
        } else {
            Toast.makeText(context,"Không có previous",Toast.LENGTH_SHORT).show()
        }

    }

    private fun handleClick(){
        binding.lnAddNewAddress.setOnClickListener {
            navController.navigate(R.id.action_myAddressFragment_to_add_AddressFragment)
        }
    }


}