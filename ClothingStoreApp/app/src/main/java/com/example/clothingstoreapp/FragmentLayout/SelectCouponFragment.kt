package com.example.clothingstoreapp.FragmentLayout

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.clothingstoreapp.Adapter.RvSelectedVoucher
import com.example.clothingstoreapp.Interface.CkbObjectInterface
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.Model.Voucher
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.VoucherService
import com.example.clothingstoreapp.ViewModel.PayOrderViewModel
import com.example.clothingstoreapp.databinding.FragmentSelectCouponBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class SelectCouponFragment : Fragment() {
   private lateinit var _binding:FragmentSelectCouponBinding
    private val binding get() = _binding
    private lateinit var db : FirebaseFirestore
    private lateinit var voucherService :VoucherService
    private lateinit var adapter : RvSelectedVoucher
    private val  sharedViewModel : PayOrderViewModel by activityViewModels()
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectCouponBinding.inflate(inflater,container,false)

        db = Firebase.firestore
        voucherService = VoucherService(db)
        initView()
        handleOnClick()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }


    private fun initView(){

        val idVoucher = sharedViewModel.voucher.value?.id
        adapter = RvSelectedVoucher(object : CkbObjectInterface<Voucher>{
            override fun isChecked(m: Voucher) {
                sharedViewModel.setVoucher(m)
            }

            override fun isNotChecked(m: Voucher) {
                sharedViewModel.setVoucher(null)
            }
        },idVoucher)

        val linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rvCoupons.adapter = adapter
        binding.rvCoupons.layoutManager = linearLayoutManager


        getData()
    }

    private fun getData(){
        val uid  = UserManager.getInstance().getUserID()
        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility =  View.VISIBLE
        voucherService.selectAllVoucherNotUse(uid!!){list->
            adapter.setData(list)

            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
        }

    }

    private fun handleOnClick(){
       binding.btnBack.setOnClickListener {
           requireActivity().onBackPressed()
       }

        binding.btnApplyInput.setOnClickListener {
            inputCode()
        }

        binding.swipRefresh.setOnRefreshListener {
            Handler().postDelayed({
                binding.edtCode.text = null
                getData()
                binding.swipRefresh.isRefreshing = false
            }, 1000)
        }
    }

    private fun inputCode(){
        if(binding.edtCode.text.toString().trim().isEmpty()){
            Toast.makeText(context,"Nhập mã voucher bạn cần tìm",Toast.LENGTH_SHORT).show()
        }else{
            binding.shimmerLayout.startLayoutAnimation()
            binding.shimmerLayout.visibility =  View.VISIBLE

            voucherService.findVoucher(binding.edtCode.text.toString().trim()){voucher,mess->
                if(voucher!=null){
                    val list = mutableListOf<Voucher>()
                    list.add(voucher)
                    adapter.setData(list)
                }else{
                    Toast.makeText(context,mess,Toast.LENGTH_SHORT).show()
                }

                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
            }
        }
    }
}