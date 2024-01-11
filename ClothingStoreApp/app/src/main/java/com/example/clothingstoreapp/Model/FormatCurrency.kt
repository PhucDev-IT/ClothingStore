package com.example.clothingstoreapp.Model

import android.annotation.SuppressLint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class FormatCurrency {
  companion object{
      private val lc = Locale("vi","VN")
      val numberFormat: NumberFormat = NumberFormat.getCurrencyInstance(lc)

      // Định dạng ngày tháng
      @SuppressLint("ConstantLocale")
      val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

      @SuppressLint("ConstantLocale")
      val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
  }
}