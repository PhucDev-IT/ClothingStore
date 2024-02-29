package com.example.clothingstoreapp.FragmentLayout


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
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
import com.example.clothingstoreapp.Model.ShippingMethod
import com.example.clothingstoreapp.Model.ShippingMethodJson
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
        apiGHN = ApiGHN.create()
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
        getInformationAddress()

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
    fun getInformationAddress(){
        AddressService().getAddressDefault{addressModel ->
            sharedViewModel.setDeliveryAddress(addressModel)
            sharedViewModel.deliveryAddress.observe(viewLifecycleOwner) { value ->
                if (value != null) {

                  //getTransportUnit()

                    calculateFeeShip(100039){total->
                        Toast.makeText(context,"Tiền: $total",Toast.LENGTH_SHORT).show()
                    }

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
        sharedViewModel.mListCart.observe(viewLifecycleOwner) { list ->
            for (item in list) {
                tongTienHang += (item.product?.quantity!!.times(item.product?.price!!))
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

            val user = UserOrder(UserManager.getInstance().getUserID()!!,UserManager.getInstance().getUserCurrent()?.tokenFCM)

            val order = OrderModel()

            order.deliveryAddress = sharedViewModel.getDeliveryAddress()
            order.carts = sharedViewModel.getListCart()
            order.orderDate = Date().time
            order.voucher = sharedViewModel.voucher.value
            order.paymentMethod = sharedViewModel.paymentMethod.value
            order.user = user
            order.orderStatus = mutableMapOf(ProgressOrder.WaitConfirmOrder.name to Date())
            order.totalMoney = tongTienHang + feeShipDefault - freeShip - voucherDiscount
            order.currentStatus = ProgressOrder.WaitConfirmOrder.name
            order.feeShip = feeShipDefault - freeShip

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


    //Lấy đơn vị vận chuyển và tính fee ship
    private fun getTransportUnit(){

        sharedViewModel.selectTransportUnit.observe(viewLifecycleOwner){unit->
            if(unit == null){
                binding.lnShowTransportUnit.visibility = View.GONE
                binding.lnKhongCoDonViVanChuyen.visibility = View.VISIBLE
            }else{
                binding.lnKhongCoDonViVanChuyen.visibility = View.GONE
                binding.lnShowTransportUnit.visibility = View.VISIBLE
                binding.tvNameTransportUnit.text = unit.shortName
                binding.tvPriceTransport.text = FormatCurrency.numberFormat.format(sharedViewModel.feeShip.value)
            }
        }


        sharedViewModel.deliveryAddress.value?.district?.let { it ->

            val call = it.districtID?.let { it1 -> apiGHN.getTransportUnit(
               // 1616,it1, 4914423
            ) }

            call?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful) {
                        val responseData = response.body()
                        if (responseData != null) {
                            // Log responseData để kiểm tra dữ liệu nhận được từ phản hồi
                            Log.d("ResponseData", responseData.toString())

                            // Tiếp tục xử lý dữ liệu nhận được
                        } else {
                            Log.e("ResponseData", "Response data is null")
                        }
                    } else {
                        Log.e("ResponseCode", "Error code: ${response.code()}")
                    }
                    if (response.isSuccessful) {
                        Toast.makeText(context,"Hellol",Toast.LENGTH_SHORT).show()
//                        val list = response.body()
//                        list.data?.let { it->
//                            val units:MutableMap<ShippingMethod,Int> = mutableMapOf()
//                            it.map {shippingMethod ->
//                                calculateFeeShip(shippingMethod.serviceId!!){fee->
//                                    units[shippingMethod] = fee
//                                }
//                            }
//                            sharedViewModel.setTransportUnit(units)
//                            units.keys.firstOrNull()
//                                ?.let { select ->
//                                    sharedViewModel.setSelectTransportUnit(select)
//
//                                }
//
//                            sharedViewModel.setFeeShip(units.values.firstOrNull())

                    //    }
                    } else {
                        response.errorBody()?.string()?.let { it1 -> Log.v("Error code 400", it1) };
                        println("Error get unit: ${response.code()}")
                        Toast.makeText(context,"Error get unit: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.message?.let { Log.e(ContentValues.TAG, it) }
                    Toast.makeText(context, "Có lỗi", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    //Tính phí vận chuyển
    private fun calculateFeeShip(service_id:Int,callback: (Int)->Unit){


        val call = apiGHN.calculateFeeShip(
            service_id,100000,"800206",1616
           ,sharedViewModel.deliveryAddress.value?.district!!.districtID!!,1000,
            10,200,15,2
        )

        call.enqueue(object : Callback<ShippingFeeResponse>{
            override fun onResponse(
                call: Call<ShippingFeeResponse>,
                response: Response<ShippingFeeResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    callback(result?.data?.total?:0)

                } else {
                    response.errorBody()?.string()?.let { it1 -> Log.v("Error code 400", it1) };
                    println("Error fee ship: ${response.code()}")
                    Toast.makeText(context,"Error fee ship: ${response.code()}", Toast.LENGTH_SHORT).show()
                    callback(0)
                }
            }

            override fun onFailure(call: Call<ShippingFeeResponse>, t: Throwable) {
                t.message?.let { Log.e(ContentValues.TAG, it) }
                callback(0)

            }
        })

    }
}