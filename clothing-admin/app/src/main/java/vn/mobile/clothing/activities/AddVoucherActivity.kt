package vn.mobile.clothing.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.mobile.clothing.R
import vn.mobile.clothing.activities.base.BaseActivity
import vn.mobile.clothing.databinding.ActivityAddVoucherBinding
import vn.mobile.clothing.models.TypeVoucher
import vn.mobile.clothing.utils.FormatCurrency
import java.util.Calendar
import java.util.Date

class AddVoucherActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding:ActivityAddVoucherBinding
    private val today: Calendar = Calendar.getInstance()
    private val typeVoucher =
        hashMapOf(
            TypeVoucher.FREESHIP.name to "Miễn phí vận chuyển",
            TypeVoucher.DISCOUNTPERCENT.name to "Giảm giá theo %",
            TypeVoucher.DISCOUNTMONEY.name to "Đơn vị VNĐ"
        )
    private var typeVoucherSelected:String? = null
    private val conditions = hashMapOf(
        "ALL" to "Tất cả đơn hàng",
        "NEW_USER" to "Mới đăng ký",
    )

    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        resetData()
    }

    override fun populateData() {

    }

    override fun setListener() {
        binding.tvEndAt.setOnClickListener(this)
        binding.tvStartAt.setOnClickListener(this)
        binding.header.toolbar.setNavigationOnClickListener { finish() }
        binding.btnAdd.setOnClickListener(this)
        binding.btnClear.setOnClickListener(this)
    }

    override val layoutView: View
        get() {
            binding = ActivityAddVoucherBinding.inflate(layoutInflater)
            return binding.root
        }

    @SuppressLint("SetTextI18n")
    private fun resetData(){
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH) + 1 // Tháng bắt đầu từ 0
        val day = today.get(Calendar.DAY_OF_MONTH)

        binding.tvStartAt.text = "$day-$month-$year"
        binding.tvEndAt.text = "$day-$month-$year"
        binding.edtTitle.text = null
        binding.edtDiscount.text = null
        binding.edtQuantity.text = null
        binding.edtDescription.text = null
        binding.edtGiveUser.text = null

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

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val adapterCondition = ArrayAdapter(this, android.R.layout.simple_spinner_item, conditions.values.toList())
        adapterCondition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCondition.adapter = adapterCondition
        binding.spinnerCondition.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
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
        val time = FormatCurrency.dateFormat.parse(str)
        if (time != null) {
            return time
        }
        return null
    }

    private fun addVoucher(){
        if(!checkData()) return
        val start = convertViewToDate(binding.tvStartAt.text.toString())
        val end = convertViewToDate(binding.tvEndAt.text.toString())


    }

    private fun checkData():Boolean{
        val start = convertViewToDate(binding.tvStartAt.text.toString())
        val end = convertViewToDate(binding.tvEndAt.text.toString())

        if(binding.edtQuantity.text.toString().trim().isEmpty() || binding.edtQuantity.text.toString().trim().toInt() <= 0){
            binding.edtQuantity.error = "Số lượng phải lớn hơn 0"
            return false
        }

        if(binding.edtDiscount.text.toString().trim().isEmpty() || binding.edtDiscount.text.toString().trim().toDouble() <= 0){
            binding.edtDiscount.error = "Phải lớn hơn 0"
            return false
        }

        if(typeVoucherSelected == TypeVoucher.DISCOUNTPERCENT.name && binding.edtDiscount.text.toString().trim().toDouble() !in 0.0 .. 100.0){
            binding.edtDiscount.error = "Phải lớn hơn 0 và nhỏ hơn 100%"
            return false
        }

        if(binding.edtTitle.text.toString().trim().isEmpty()){
            binding.edtTitle.error = "Không được để trống"
            return false
        }

        if(binding.edtDescription.text.toString().trim().isEmpty()){
            binding.edtDescription.error = "Không được để trống"
            return false
        }

        if(end!! > start!!){
            binding.tvEndAt.error = "Thời gian kết thúc phải lớn hoơn thời gian bắt đầu"
            return false
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_start_at -> handleChooseDate(binding.tvStartAt)
            R.id.tv_end_at -> handleChooseDate(binding.tvEndAt)
            R.id.btn_clear -> resetData()
            R.id.btn_add -> addVoucher()
        }
    }
}