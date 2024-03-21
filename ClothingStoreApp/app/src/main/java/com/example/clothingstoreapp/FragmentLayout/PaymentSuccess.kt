package com.example.clothingstoreapp.FragmentLayout

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.clothingstoreapp.Activity.PurchasedHistoryScreen
import com.example.clothingstoreapp.Model.FormatCurrency
import com.example.clothingstoreapp.Model.Notification
import com.example.clothingstoreapp.Model.PushNotification
import com.example.clothingstoreapp.Model.UserManager
import com.example.clothingstoreapp.Service.CartService
import com.example.clothingstoreapp.Service.NotificationService
import com.example.clothingstoreapp.ViewModel.PayOrderViewModel
import com.example.clothingstoreapp.databinding.FragmentPaymentSuccessBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.Date


class PaymentSuccess : Fragment() {
   private lateinit var _binding:FragmentPaymentSuccessBinding
    private val binding get() = _binding
    private val sharedViewModel: PayOrderViewModel by activityViewModels()
    private val notificationService: NotificationService = NotificationService()
    private val db:FirebaseFirestore = Firebase.firestore
    private val userID: String? = UserManager.getInstance().getUserID()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentSuccessBinding.inflate(inflater,container,false)

        initView()

        binding.btnViewOrder.setOnClickListener {
            val intent = Intent(context,PurchasedHistoryScreen::class.java)
            intent.putExtra("allowOnBackPress",false)
            startActivity(intent)
            requireActivity().finishAffinity()
        }

        return binding.root
    }



    //Thông báo đến người dùng
    private fun initView(){
        context?.let {
            PushNotification().sendNotification(it,"Đặt hàng thành công","Bạn có một đơn hàng mới")
        }

        removeCart()
        sendNotificationToAdmin()
    }

    //Gửi thông báo


    //Gửi thông báo đến admin
    private fun sendNotificationToAdmin(){

        val order = sharedViewModel.order.value
        val user = UserManager.getInstance().getUserCurrent()

        val notification = Notification()
        notification.title = "Đơn hàng mới!"
        notification.content = "\"${user?.fullName}\" vừa đặt ${order?.products?.size} sản phẩm với " +
                "${FormatCurrency.numberFormat.format(order?.totalMoney)}. Xác nhận đơn hàng ngay."
        notification.timeSend = Date()
        notification.img = order?.products?.get(0)?.imgPreview.toString()
        notification.isRead = false

        notificationService.sendNotificationToAdmin(notification){b->
            if(b){
                Log.d(ContentValues.TAG,"Send is success")
            }else{
                Log.d(ContentValues.TAG,"Send is failed")
            }
        }

    }

    //Xóa sản phẩm trong giỏ hàng sau khi mua
    private fun removeCart(){
        sharedViewModel.getListCart()?.let {
            if (userID != null) {
                CartService(db).removeItemCart(it,userID){b->
                    if(b) Log.d(TAG,"Xóa thành công")
                    else Log.e(TAG,"Có lỗi")
                }
            }
        }
    }


}