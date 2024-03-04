package com.example.clothingstoreadmin.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.adapter.CustomDialog
import com.example.clothingstoreadmin.api.ApiNotificationService
import com.example.clothingstoreadmin.databinding.ActivityAddNewVoucherScreenBinding
import com.example.clothingstoreadmin.model.Data
import com.example.clothingstoreadmin.model.DataMessageNotification
import com.example.clothingstoreadmin.model.NotificationModel
import com.example.clothingstoreadmin.model.TypeVoucher
import com.example.clothingstoreadmin.model.Voucher
import com.example.clothingstoreadmin.service.VoucherService
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

class AddNewVoucherScreen : AppCompatActivity() {
    private lateinit var binding:ActivityAddNewVoucherScreenBinding
    private lateinit var db:FirebaseFirestore
    private lateinit var voucherService: VoucherService
    @SuppressLint("SimpleDateFormat")
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
    private var typeVoucherSelected: String? = null
    private lateinit var customDialog: CustomDialog
    private val today: Calendar = Calendar.getInstance()

    private val typeVoucher =
        hashMapOf(
            TypeVoucher.FREESHIP.name to "Miễn phí vận chuyển",
            TypeVoucher.DISCOUNTPERCENT.name to "Giảm giá theo %",
            TypeVoucher.DISCOUNTMONEY.name to "Giảm giá theo tiền"
        )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewVoucherScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customDialog = CustomDialog(this)
        db = Firebase.firestore
        voucherService = VoucherService(db)
        initView()
        handleClick()
    }

    @SuppressLint("SetTextI18n")
    private fun initView(){
        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                typeVoucher.values.toList()
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTypeVoucher.adapter = adapter

        binding.spinnerTypeVoucher.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                typeVoucherSelected = typeVoucher.keys.elementAt(p2)

                if (p2 == 0) {
                    binding.textLayoutReduce.visibility = View.INVISIBLE

                } else {
                    binding.textLayoutReduce.visibility = View.VISIBLE
                    if (p2 == 1) {
                        binding.edtReduceMoney.setText("10")
                    } else {
                        binding.edtReduceMoney.setText("10000")
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH) + 1 // Tháng bắt đầu từ 0
        val day = today.get(Calendar.DAY_OF_MONTH)

        binding.tvTimeStart.text = "$day-$month-$year"
        binding.tvTimeEnd.text = "$day-$month-$year"

    }

    @SuppressLint("SetTextI18n")
    private fun handleChooseDate(view: TextView) {
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
            view.text = "$i3-${i2 + 1}-$i"
        }, year, month, day).show()

    }

    private fun convertViewToDate(str: String): Date? {
        val time = dateFormat.parse(str)
        if (time != null) {
            return time
        }
        return null
    }


    private fun handleClick(){
        binding.tvTimeStart.setOnClickListener {
            handleChooseDate(binding.tvTimeStart)
        }

        binding.tvTimeEnd.setOnClickListener {
            handleChooseDate(binding.tvTimeEnd)
        }

        binding.btnAdd.setOnClickListener {
            addVoucher()
        }

        binding.btnClear.setOnClickListener {
            refreshData()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun generateRandomId(): String {
        val charPool : List<Char> = ('A'..'Z') + ('0'..'9')
        return (1..10)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun checkVoucher(voucher: Voucher){
        val id = generateRandomId()
        voucherService.checkIdAvailability(id){b->
            if(b){
                checkVoucher(voucher)
            }else{
                voucher.id = id
                voucherService.addVoucher(id,voucher){result,mess->
                    if(result){
                        Toast.makeText(this,"Thêm thành công",Toast.LENGTH_SHORT).show()
                        refreshData()
                        voucher.title?.let { sendNotificationToAllUser(it) }
                    }else{
                        Toast.makeText(this,mess,Toast.LENGTH_SHORT).show()
                    }

                }
            }
            customDialog.closeDialog()
        }
    }

    private fun addVoucher(){

        val start = convertViewToDate(binding.tvTimeStart.text.toString())
        val end = convertViewToDate(binding.tvTimeEnd.text.toString())

        if(binding.edtQuantity.text.toString().trim().isEmpty() || binding.edtQuantity.text.toString().trim().toInt() <= 0){
            binding.edtQuantity.error = "Số lượng phải lớn hơn 0"
        }else if((typeVoucherSelected == TypeVoucher.DISCOUNTPERCENT.name &&
            (binding.edtReduceMoney.text.toString().trim().toDouble() <=0 || binding.edtReduceMoney.text.toString().trim().toDouble() >100))
            || binding.edtReduceMoney.text.toString().trim().isEmpty()){
            binding.edtReduceMoney.error = "Phải lớn hơn 0 và nhỏ hơn 100%"
        }else if((typeVoucherSelected == TypeVoucher.DISCOUNTMONEY.name &&
                    (binding.edtReduceMoney.text.toString().trim().toDouble() <=0 || binding.edtReduceMoney.text.toString().trim().toDouble() >100000000))
            || binding.edtReduceMoney.text.toString().trim().isEmpty()){
            binding.edtReduceMoney.error = "Số tiền phải lớn hơn 0 và nhỏ hơn 100000000"
        }else if(end!! > start!!){
            binding.tvTimeEnd.error = "Thời gian kết thúc phải lớn hoơn thời gian bắt đầu"
        }else if(binding.edtDescription.text.toString().trim().isEmpty()){
            binding.edtDescription.error = "Không được để trống"
        }else{

            val voucher = Voucher()
            voucher.quantity = binding.edtQuantity.text.toString().trim().toInt()
            voucher.typeVoucher = typeVoucherSelected
            voucher.discount = binding.edtReduceMoney.text.toString().trim().toDouble()
            voucher.timeStart = start
            voucher.timeEnd = end
            voucher.content = binding.edtDescription.text.toString().trim()

            when(typeVoucherSelected){
                TypeVoucher.DISCOUNTPERCENT.name -> voucher.title = "Giảm ${binding.edtReduceMoney.text.toString().trim().toInt()} %"
                TypeVoucher.DISCOUNTMONEY.name ->  voucher.title = "Giảm ${voucher.discount} đ"
                else -> voucher.title = "Miễn phí vận chuyển"
            }

            customDialog.dialogLoadingBasic("Quá trình có thể mất vài phút dựa vào tốc độ mạng của bạn\nKhông đóng ứng dụng")
            checkVoucher(voucher)

        }
    }

    //Gửi thông báo cho nguười dùng
    private fun sendNotificationToAllUser(value:String){
        val call = ApiNotificationService.create()
        call.sendNotification(DataMessageNotification("/topics/allDevices",
            Data("Voucher đến tay, mua ngay ưu đãi","Siêu ưu đãi với voucher $value")))
            .enqueue(object : Callback<DataMessageNotification>{
                override fun onResponse(
                    call: Call<DataMessageNotification>?,
                    response: Response<DataMessageNotification>?
                ) {
                    Log.d(TAG,"Gửi thành công")
                }

                override fun onFailure(call: Call<DataMessageNotification>?, t: Throwable?) {
                    Log.e(TAG,"Có lỗi xảy ra: ${t?.message}")
                }
            })
    }




    //-------------------- reset--------------------
    fun refreshData() {

        binding.edtQuantity.setText("1")
        binding.edtReduceMoney.setText("1")
        binding.edtDescription.setText("")
        binding.tvTimeStart.text = dateFormat.format(today.time)
        binding.tvTimeEnd.text = dateFormat.format(today.time)
    }
}