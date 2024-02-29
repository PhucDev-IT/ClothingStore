package com.example.clothingstoreapp.FragmentLayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.clothingstoreapp.API.ApiGHN

import com.example.clothingstoreapp.Model.AddressModel
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.AddressService
import com.example.clothingstoreapp.databinding.FragmentAddAddressBinding

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.clothingstoreapp.ViewModel.NewAddressViewModel


class Add_AddressFragment : Fragment() {
    private lateinit var _binding: FragmentAddAddressBinding
    private val binding get() = _binding
    private lateinit var apiService: ApiGHN
    private val sharedViewModel: NewAddressViewModel by activityViewModels()
    private lateinit var navController: NavController
    private val typesAddress = listOf("Home", "Office", "Parent's House", "Friend's House")
    private val addressService:AddressService = AddressService()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddAddressBinding.inflate(inflater, container, false)

        // Tạo instance của RetrofitFactory
        apiService = ApiGHN.create()
        initUI()
        handleClick()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }


    private fun initUI() {
        sharedViewModel.informationAddress.observe(viewLifecycleOwner) {
            if (sharedViewModel.checkEmpty()) {
                binding.tvInformationAddress.text = it
            }
        }


    }

    private fun handleClick() {
        binding.btnFinish.setOnClickListener {
            addAddress()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.lnSelectAddress.setOnClickListener {
            navController.navigate(R.id.action_add_AddressFragment_to_selectNewAddressFragment)
        }

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permistion ->
            when {
                permistion.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                        permistion.getOrDefault(
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            false
                        ) -> {


                }
            }
        }

        binding.lnUseLocalAddress.setOnClickListener {
//            locationPermissionRequest.launch{
//                arrayOf(
//                   android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                   android.Manifest.permission.ACCESS_FINE_LOCATION
//                )
//            }
        }
    }


    private fun addAddress() {
        if (binding.edtFullName.text.toString().trim().isEmpty()) {
            binding.edtFullName.error = "Chưa nhập họ và tên"
        } else if (binding.edtNumberPhone.text.toString().trim().isEmpty()) {
            binding.edtNumberPhone.error = "Trường này là bắt buộc"
        } else if (binding.edtDetailsAddress.text.toString().trim().isEmpty()) {
            binding.edtDetailsAddress.error = "Trường này là bắt buộc"
        } else {

            var type = "Home"
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    type = typesAddress[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Xử lý khi không có mục nào được chọn
                }
            }

            val address = AddressModel(
                binding.edtFullName.text.toString().trim(),
                binding.edtNumberPhone.text.toString().trim(),
                sharedViewModel.province.value,
                sharedViewModel.district.value,
                sharedViewModel.ward.value,
                binding.edtDetailsAddress.text.toString().trim(),
                type
            )

            val customer = UserManager.getInstance().getUserCurrent()

            val listAddress = mutableListOf<AddressModel>()
            if (binding.switchUseDefault.isChecked || customer?.listAddress == null) {
                if (customer?.listAddress != null) {
                    for (item in customer.listAddress!!) {
                        item.isDefault = false
                    }
                    listAddress.addAll(customer.listAddress!!)
                }
                address.isDefault = true
            }
            listAddress.add(address)

            if (customer != null) {
                customer.listAddress = listAddress
                UserManager.getInstance().setUser(customer)
            }

            addressService.addNewAddress(address) { reuslt ->
                if (reuslt) {
                    Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else {
                    Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
