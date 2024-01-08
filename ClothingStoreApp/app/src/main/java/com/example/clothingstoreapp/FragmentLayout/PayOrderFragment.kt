package com.example.clothingstoreapp.FragmentLayout


import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.clothingstoreapp.Model.UserOrder
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

    private var amount = "10000"
    private val fee = "0"
    var environment = 0 //developer default

    private val merchantName = "Demo SDK"
    private val merchantCode = "SCB01"
    private val merchantNameLabel = "Nhà cung cấp"


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

       // AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION


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

            val user = UserOrder(UserManager.getInstance().getUserID()!!,UserManager.getInstance().getUserCurrent()?.tokenFCM)

            val order = OrderModel()


            order.deliveryAddress = sharedViewModel.getDeliveryAddress()
            order.carts = sharedViewModel.getListCart()
            order.orderDate = Date()
            order.voucher = sharedViewModel.voucher.value
            order.paymentMethod = sharedViewModel.paymentMethod.value
            order.user = user
            order.orderStatus = mutableMapOf(ProgressOrder.WaitConfirmOrder.name to Date())
            order.totalMoney = tongTienHang + phiVanChuyen - freeShip - voucherDiscount
            order.currentStatus = ProgressOrder.WaitConfirmOrder.name
            order.feeShip = freeShip

            orderSevice.addOrder(order) { b ->
                if (b) {
                    navController.navigate(R.id.action_payOrderFragment_to_paymentSuccess)

                } else {
                    Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                }
                customDialog.closeDialog()
            }

        }
    }


    //Thanh toán bằng momo
    //Get token through MoMo app
//    private  fun requestPayment(order:OrderModel) {
//        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT)
//        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN)
//
//        val eventValue: MutableMap<String, Any> = HashMap()
//        //client Required
//        eventValue["merchantname"] =
//            merchantName //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
//        eventValue["merchantcode"] =
//            merchantCode //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
//        eventValue["amount"] = amount //Kiểu integer
//        eventValue["orderId"] =
//            "orderId123456789" //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
//        eventValue["orderLabel"] = "Mã đơn hàng" //gán nhãn
//
//        //client Optional - bill info
//        eventValue["merchantnamelabel"] = "Dịch vụ" //gán nhãn
//        eventValue["fee"] = order.feeShip!!.toInt()
//        eventValue["description"] = "Thanh toán cho đơn hàng XYZ" //mô tả đơn hàng - short description
//
//        //client extra data
//        eventValue["requestId"] = merchantCode + "merchant_billId_" + System.currentTimeMillis()
//        eventValue["partnerCode"] = merchantCode
//        //Example extra data
//        val objExtraData = JSONObject()
//        try {
//            objExtraData.put("site_code", "008")
//            objExtraData.put("site_name", "CGV Cresent Mall")
//            objExtraData.put("screen_code", 0)
//            objExtraData.put("screen_name", "Special")
//            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3")
//            objExtraData.put("movie_format", "2D")
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        eventValue["extraData"] = objExtraData.toString()
//        eventValue["extra"] = ""
//        AppMoMoLib.getInstance().requestMoMoCallBack(requireActivity(), eventValue)
//    }
//
//    //Get token callback from MoMo app an submit to server side
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
//            if (data != null) {
//                if (data.getIntExtra("status", -1) == 0) {
//                    //TOKEN IS AVAILABLE
//                    Toast.makeText(context,"message: " + "Get token " + data.getStringExtra("message"),Toast.LENGTH_SHORT).show()
//
//                    val token = data.getStringExtra("data") //Token response
//                    val phoneNumber = data.getStringExtra("phonenumber")
//                    var env = data.getStringExtra("env")
//                    if (env == null) {
//                        env = "app"
//                    }
//                    if (token != null && token != "") {
//                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
//                        // IF Momo topup success, continue to process your order
//                    } else {
//                        Toast.makeText(context,"Có lỗi xảy ra",Toast.LENGTH_SHORT).show()
//
//                    }
//                } else if (data.getIntExtra("status", -1) == 1) {
//                    //TOKEN FAIL
//                    val message =
//                        if (data.getStringExtra("message") != null) data.getStringExtra("message") else "Thất bại"
//                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
//                } else if (data.getIntExtra("status", -1) == 2) {
//                    //TOKEN FAIL
//                    Toast.makeText(context,"Có lỗi xảy ra",Toast.LENGTH_SHORT).show()
//                } else {
//                    //TOKEN FAIL
//                    Toast.makeText(context,"Có lỗi xảy ra",Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(context,"Có lỗi xảy ra",Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            Toast.makeText(context,"Có lỗi xảy ra",Toast.LENGTH_SHORT).show()
//        }
//    }
}