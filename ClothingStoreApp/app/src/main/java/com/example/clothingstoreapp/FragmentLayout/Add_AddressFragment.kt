package com.example.clothingstoreapp.FragmentLayout

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.clothingstoreapp.API.ApiService

import com.example.clothingstoreapp.Model.AddressModel
import com.example.clothingstoreapp.Model.DistrictsJson
import com.example.clothingstoreapp.Model.ProvincesJson
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.Model.WardJson
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.AddressService
import com.example.clothingstoreapp.databinding.FragmentAddAddressBinding
import com.google.android.gms.location.FusedLocationProviderClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.activity.result.contract.ActivityResultContracts


class Add_AddressFragment : Fragment() {
    private lateinit var _binding: FragmentAddAddressBinding
    private val binding get() = _binding
    private lateinit var listProvincesJson: List<ProvincesJson>
    private lateinit var listDistrics: List<DistrictsJson>
    private lateinit var apiService: ApiService
    private lateinit var listWard: List<WardJson>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var addressService: AddressService = AddressService()
    private val typesAddress = listOf("Home", "Office", "Parent's House", "Friend's House")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddAddressBinding.inflate(inflater, container, false)

        // Tạo instance của ApiService
        apiService = ApiService.create()
        initView()
        CallApiProvinces()
        handleClick()

        return binding.root
    }


    private fun handleClick(){
        binding.btnFinish.setOnClickListener {
            addAddress()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){permistion->
            when{
                permistion.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION,false)||
                        permistion.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION,false)->{


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

    //Lấy danh sách thành phố
    private fun CallApiProvinces() {

        val call = apiService.callApiProvinces()

        call.enqueue(object : Callback<List<ProvincesJson>> {
            override fun onResponse(
                call: Call<List<ProvincesJson>>?,
                response: Response<List<ProvincesJson>>?
            ) {
                listProvincesJson = response?.body()!!
                val names =
                    listProvincesJson.map { it.name } // Lấy danh sách tên từ danh sách ProvincesJson
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
                binding.atvTinhThanhPho.setAdapter(adapter)
                Toast.makeText(context, "Size: ${names.size}", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<List<ProvincesJson>>?, t: Throwable?) {
                Toast.makeText(context, "Có lỗi", Toast.LENGTH_SHORT).show()
            }
        })

        binding.atvTinhThanhPho.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) binding.atvTinhThanhPho.showDropDown()
        }

        binding.atvTinhThanhPho.setOnItemClickListener { parent, view, position, id ->
            for (item in listProvincesJson) {
                if (item.name == binding.atvTinhThanhPho.text.toString().trim()) {
                    callApiDistric(item.code)
                    break
                }
            }
        }
    }

    //Lấy danh sách quận huyện
    private fun callApiDistric(code: Int) {
        val call = apiService.callApiDistrics(code, 2)

        call.enqueue(object : Callback<ProvincesJson> {
            override fun onResponse(
                call: Call<ProvincesJson>?,
                response: Response<ProvincesJson>?
            ) {
                val result = response?.body()!!

                listDistrics = result.districts
                val names =
                    listDistrics.map { it.name } // Lấy danh sách tên từ danh sách ProvincesJson
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
                binding.autoDistric.setAdapter(adapter)
                Toast.makeText(context, "Size: ${names.size}", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<ProvincesJson>?, t: Throwable?) {
                Toast.makeText(context, "Có lỗi", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Fail: ${t?.message}")
            }
        })

        binding.autoDistric.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) binding.autoDistric.showDropDown()
        }

        binding.autoDistric.setOnItemClickListener { parent, view, position, id ->
            for (item in listDistrics) {
                if (item.name == binding.autoDistric.text.toString().trim()) {
                    callApiWard(item.code)
                    break
                }
            }
        }
    }

    //Lấy tên phường xã
    private fun callApiWard(code: Int) {
        val call = apiService.callApiWard(code, 2)

        call.enqueue(object : Callback<DistrictsJson> {
            override fun onResponse(
                call: Call<DistrictsJson>?,
                response: Response<DistrictsJson>?
            ) {
                val result = response?.body()!!

                listWard = result.wards
                val names = listWard.map { it.name } // Lấy danh sách tên từ danh sách ProvincesJson
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
                binding.autoWard.setAdapter(adapter)
                Toast.makeText(context, "Size: ${names.size}", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<DistrictsJson>?, t: Throwable?) {
                Toast.makeText(context, "Có lỗi", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Fail: ${t?.message}")
            }
        })

        binding.autoWard.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) binding.autoWard.showDropDown()
        }

        binding.autoWard.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(context, "Bạn chọn: ${id}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView() {

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typesAddress)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
    }

    private fun addAddress() {
        if (binding.edtFullName.text.toString().trim().isEmpty()) {
            binding.edtFullName.error = "Chưa nhập họ và tên"
        } else if (binding.edtNumberPhone.text.toString().trim().isEmpty()) {
            binding.edtNumberPhone.error = "Trường này là bắt buộc"
        } else if (binding.atvTinhThanhPho.text.toString().trim().isEmpty()) {
            binding.atvTinhThanhPho.error = "Trường này là bắt buộc"

        } else if (binding.autoDistric.text.toString().trim().isEmpty()) {
            binding.autoDistric.error = "Trường này là bắt buộc"

        } else if (binding.autoWard.text.toString().trim().isEmpty()) {
            binding.autoWard.error = "Trường này là bắt buộc"
        } else if (binding.edtDetailsAddress.text.toString().trim().isEmpty()) {
            binding.edtDetailsAddress.error = "Trường này là bắt buộc"
        } else {

            var type = "Home"
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                    type = typesAddress[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Xử lý khi không có mục nào được chọn
                }
            }

            val address = AddressModel(
                binding.edtFullName.text.toString().trim(),
                binding.edtNumberPhone.text.toString().trim(),
                binding.atvTinhThanhPho.text.toString(),
                binding.autoDistric.text.toString(),
                binding.autoWard.text.toString().trim(),
                binding.edtDetailsAddress.text.toString().trim(),
                type
            )

            val customer = UserManager.getInstance().getUserCurrent()

            if (customer != null) {
                if (binding.switchUseDefault.isChecked || customer.listAddress == null) {
                    customer.defaultAddress = address
                }

                val listAddress = mutableListOf<AddressModel>(address)
                customer.listAddress?.let { listAddress.addAll(it) }

                customer.listAddress = listAddress

                UserManager.getInstance().setUser(customer)

                addressService.addNewAddress(customer) { reuslt ->
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
}