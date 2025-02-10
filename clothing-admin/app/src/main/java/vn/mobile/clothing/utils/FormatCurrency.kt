package vn.mobile.clothing.utils

import android.annotation.SuppressLint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormatCurrency {
  companion object{
      const val ISO8601DATEFORMAT: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"

      private val lc = Locale("vi","VN")
      val numberFormat: NumberFormat = NumberFormat.getCurrencyInstance(lc)

      // Định dạng ngày tháng
      @SuppressLint("ConstantLocale")
      val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

      @SuppressLint("ConstantLocale")
      val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())



      fun stringToDate(date: String, dateFormat: String?): Date? {
          return try {
              @SuppressLint("SimpleDateFormat") val format =
                  SimpleDateFormat(dateFormat, Locale.getDefault())
              format.parse(date)
          } catch (ex: Exception) {
              null
          }
      }
  }
}