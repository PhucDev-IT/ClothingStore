package com.example.clothingstoreapp.FragmentLayout

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.CustomDialog
import com.example.clothingstoreapp.Adapter.RvCheckoutAdapter
import com.example.clothingstoreapp.Model.FormatCurrency
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.Model.ProgressOrder
import com.example.clothingstoreapp.Model.TypeVoucher
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.OrderService
import com.example.clothingstoreapp.ViewModel.PayOrderViewModel
import com.example.clothingstoreapp.databinding.FragmentPayOrderBinding
import java.util.Date


class PayOrderFragment : Fragment() {
    private lateinit var _binding: FragmentPayOrderBinding
    private val binding get() = _binding
    private lateinit var navController: NavController
    private val sharedViewModel: PayOrderViewModel by activityViewModels()
    private val phiVanChuyen: Double = 16000.0
    private var freeShip: Double = 0.0
    private var voucherDiscount: Double = 0.0
    private var tongTienHang: Double = 0.0
    private val user = UserManager.getInstance().getUserCurrent()
    private lateinit var customDialog: CustomDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPayOrderBinding.inflate(inflater, container, false)
        customDialog = context?.let { CustomDialog(it) }!!
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

        sharedViewModel.mListCart.observe(viewLifecycleOwner) { list ->
            val adapter = RvCheckoutAdapter(list)
            val linearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvProducts.adapter = adapter
            binding.rvProducts.layoutManager = linearLayoutManager
        }

        sharedViewModel.voucher.observe(viewLifecycleOwner) { voucher ->
            if (voucher == null) {
                binding.tvChooseVoucher.text = "Chọn mã voucher"
            } else {

                when (voucher.typeVoucher) {
                    TypeVoucher.FREESHIP.name -> freeShip = phiVanChuyen

                    TypeVoucher.DISCOUNTMONEY.name -> voucherDiscount = (voucher.discount ?: 0.0)

                    TypeVoucher.DISCOUNTPERCENT.name -> {
                        val percent = (voucher.discount ?: 1.0).div(100)
                        voucherDiscount = (tongTienHang + phiVanChuyen).times(percent)
                    }
                }

                binding.tvChooseVoucher.text = voucher.id

                binding.tvGiamGiaVanChuyen.text =
                    "- " + FormatCurrency.numberFormat.format(freeShip)
                binding.tvVoucherGiamGia.text =
                    "- " + FormatCurrency.numberFormat.format(voucherDiscount)
                binding.tvTongTienThanhToan.text =
                    FormatCurrency.numberFormat.format(tongTienHang + phiVanChuyen - freeShip - voucherDiscount)
                binding.tvTotalMoney.text = binding.tvTongTienThanhToan.text
            }
        }

        if (user?.defaultAddress != null) {

            sharedViewModel.setDeliveryAddress(user.defaultAddress!!)

        }

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
                    "${value.phuongXa}, ${value.quanHuyen}, ${value.tinhThanhPho}"
            } else {
                binding.lnShowInformationUser.visibility = View.GONE
                binding.lnNotAddress.visibility = View.VISIBLE
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

    @SuppressLint("SetTextI18n")
    private fun calculateOrder() {
        tongTienHang = 0.0
        sharedViewModel.mListCart.observe(viewLifecycleOwner) { list ->
            for (item in list) {
                tongTienHang += (item.quantity?.times(item.product?.price!!)!!)
            }

            binding.tvSumMoneyProduct.text = FormatCurrency.numberFormat.format(tongTienHang)
            binding.tvTongPhiVanChuyen.text = FormatCurrency.numberFormat.format(phiVanChuyen)
            binding.tvGiamGiaVanChuyen.text = "- " + FormatCurrency.numberFormat.format(freeShip)
            binding.tvVoucherGiamGia.text =
                "- " + FormatCurrency.numberFormat.format(voucherDiscount)
            binding.tvTongTienThanhToan.text =
                FormatCurrency.numberFormat.format(tongTienHang + phiVanChuyen - freeShip - voucherDiscount)
            binding.tvTotalMoney.text = binding.tvTongTienThanhToan.text

        }
    }


    //Mua hàng
    private fun payOrder() {
        if (sharedViewModel.getDeliveryAddress() == null) {
            Toast.makeText(context, "Thiếu thông tin nhận hàng", Toast.LENGTH_SHORT).show()
        } else {
            customDialog.dialogBasic("Đang xử lý...")
            val orderSevice = OrderService()

            val order = OrderModel()

            order.deliveryAddress = sharedViewModel.getDeliveryAddress()
            order.carts = sharedViewModel.getListCart()
            order.orderDate = Date()
            order.voucher = sharedViewModel.voucher.value
            order.paymentMethod = sharedViewModel.paymentMethod.value
            order.userID = UserManager.getInstance().getUserID()
            order.orderStatus = mapOf(ProgressOrder.WaitConfirmOrder.name to Date())
            order.totalMoney = tongTienHang + phiVanChuyen - freeShip - voucherDiscount

            orderSevice.addOrder(order) { b ->
                if (b) {
                    navController.navigate(R.id.action_payOrderFragment_to_paymentSuccess)
                    requireActivity().finishAffinity()
                } else {
                    Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                }
                customDialog.closeDialog()
            }

        }
    }

}