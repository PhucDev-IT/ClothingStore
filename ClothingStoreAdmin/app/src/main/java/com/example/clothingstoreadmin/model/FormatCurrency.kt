package com.example.clothingstoreadmin.model

import android.annotation.SuppressLint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class FormatCurrency {
  companion object{
      val lc = Locale("vi","VN")
      val numberFormat = NumberFormat.getCurrencyInstance(lc)

      // Định dạng ngày tháng
      @SuppressLint("ConstantLocale")
      val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

      val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

      val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
  }
}