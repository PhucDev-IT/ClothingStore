package com.example.clothingstoreapp.FragmentLayout


import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.API.ApiGHN
import com.example.clothingstoreapp.Adapter.CustomDialog
import com.example.clothingstoreapp.Adapter.RvCheckoutAdapter
import com.example.clothingstoreapp.Model.FormatCurrency
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.ProgressOrder
import com.example.clothingstoreapp.Model.ShippingFeeResponse
import com.example.clothingstoreapp.Model.TypeVoucher
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.Model.UserOrder
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.AddressService

import com.example.clothingstoreapp.Service.OrderService
import com.example.clothingstoreapp.ViewModel.PayOrderViewModel
import com.example.clothingstoreapp.databinding.FragmentPayOrderBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date


class PayOrderFragment : Fragment() {
    private lateinit var _binding: FragmentPayOrderBinding
    private val binding get() = _binding
    private lateinit var navController: NavController

    private val sharedViewModel: PayOrderViewModel by activityViewModels()
    private val feeShipDefault: Double = 16000.0
    private var freeShip: Double = 0.0
    private var voucherDiscount: Double = 0.0
    private var tongTienHang: Double = 0.0
    private val user = UserManager.getInstance().getUserCurrent()
    private lateinit var customDialog: CustomDialog
    private lateinit var apiGHN: ApiGHN



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPayOrderBinding.inflate(inflater, container, false)
        customDialog = context?.let { CustomDialog(it) }!!
        apiGHN = ApiGHN.create()
        calculateOrder()
        initView()
        handleClick()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }


    @SuppressLint("SetTextI18n")
    private fun initView() {
        getInformationAddress()

        val products = sharedViewModel.getListCart()

        val adapter = products?.let { RvCheckoutAdapter(it) }
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.layoutManager = linearLayoutManager


        sharedViewModel.voucher.observe(viewLifecycleOwner) { voucher ->
            if (voucher == null) {
                binding.tvChooseVoucher.text = "Chọn mã voucher"
            } else {

                when (voucher.typeVoucher) {
                    TypeVoucher.FREESHIP.name -> freeShip = feeShipDefault

                    TypeVoucher.DISCOUNTMONEY.name -> voucherDiscount = (voucher.discount ?: 0.0)

                    TypeVoucher.DISCOUNTPERCENT.name -> {
                        val percent = (voucher.discount ?: 1.0).div(100)
                        voucherDiscount = (tongTienHang + feeShipDefault).times(percent)
                    }
                }

                binding.tvChooseVoucher.text = voucher.id

                binding.tvGiamGiaVanChuyen.text =
                    "- " + FormatCurrency.numberFormat.format(freeShip)
                binding.tvVoucherGiamGia.text =
                    "- " + FormatCurrency.numberFormat.format(voucherDiscount)
                binding.tvTongTienThanhToan.text =
                    FormatCurrency.numberFormat.format(tongTienHang + feeShipDefault - freeShip - voucherDiscount)
                binding.tvTotalMoney.text = binding.tvTongTienThanhToan.text
            }
        }


    }

    private fun handleClick() {
        binding.btnChange.setOnClickListener {
            navController.navigate(R.id.action_payOrderFragment_to_optionAddressFragment)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvChooseVoucher.setOnClickListener {
            navController.navigate(R.id.action_payOrderFragment_to_selectCouponFragment)
        }

        binding.tvChoosePayMethod.setOnClickListener {
            navController.navigate(R.id.action_payOrderFragment_to_selectPaymentMethodsFragment)
        }

        binding.btnBuyProduct.setOnClickListener {
            payOrder()
        }
    }


    //Lấy thông tin địa chỉ
    @SuppressLint("SetTextI18n")
    fun getInformationAddress() {
        AddressService().getAddressDefault { addressModel ->
            sharedViewModel.setDeliveryAddress(addressModel)
            sharedViewModel.deliveryAddress.observe(viewLifecycleOwner) { value ->
                if (value != null) {

                    binding.lnShowInformationUser.visibility = View.VISIBLE
                    binding.lnNotAddress.visibility = View.GONE

                    binding.tvTypeAddress.text = value.typeAddress
                    binding.tvFullName.text = value.fullName
                    binding.tvNumberPhone.text = value.numberPhone
                    binding.tvDetailsAddress.text = value.addressDetails
                    binding.tvTypeAddress.text = value.typeAddress
                    binding.tvInforAddress.text =
                        "${value.ward?.WardName}, ${value.district?.districtName}, ${value.province?.ProvinceName}"
                } else {
                    binding.lnShowInformationUser.visibility = View.GONE
                    binding.lnNotAddress.visibility = View.VISIBLE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateOrder() {
        tongTienHang = 0.0
        sharedViewModel.mCarts.observe(viewLifecycleOwner) { list ->
            for (item in list) {
                tongTienHang += (item.quantity.times(item.price!!))
            }

            binding.tvSumMoneyProduct.text = FormatCurrency.numberFormat.format(tongTienHang)
            binding.tvTongPhiVanChuyen.text = FormatCurrency.numberFormat.format(feeShipDefault)
            binding.tvGiamGiaVanChuyen.text = "- " + FormatCurrency.numberFormat.format(freeShip)
            binding.tvVoucherGiamGia.text =
                "- " + FormatCurrency.numberFormat.format(voucherDiscount)
            binding.tvTongTienThanhToan.text =
                FormatCurrency.numberFormat.format(tongTienHang + feeShipDefault - freeShip - voucherDiscount)
            binding.tvTotalMoney.text = binding.tvTongTienThanhToan.text

        }
    }


    //Mua hàng
    private fun payOrder() {
        if (sharedViewModel.getDeliveryAddress() == null) {
            Toast.makeText(context, "Thiếu thông tin nhận hàng", Toast.LENGTH_SHORT).show()
        } else {
            customDialog.dialogLoadingBasic("Đang xử lý...")
            val orderSevice = OrderService()

            val user = UserOrder(
                UserManager.getInstance().getUserID()!!,
                UserManager.getInstance().getUserCurrent()?.tokenFCM
            )

            val order = OrderModel()

            order.deliveryAddress = sharedViewModel.getDeliveryAddress()
            order.orderDate = Date().time
            order.voucher = sharedViewModel.voucher.value
            order.paymentMethod = sharedViewModel.paymentMethod.value
            order.user = user
            order.totalMoney = tongTienHang + feeShipDefault - freeShip - voucherDiscount
            order.currentStatus = ProgressOrder.WaitConfirmOrder.name
            order.feeShip = feeShipDefault - freeShip
            order.products = sharedViewModel.getListCart()

            orderSevice.addOrder(order) { b ->
                if (b) {
                    sharedViewModel.setOrder(order)
                    navController.navigate(R.id.action_payOrderFragment_to_paymentSuccess)

                } else {
                    Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                }
                customDialog.closeDialog()

            }

        }
    }

}