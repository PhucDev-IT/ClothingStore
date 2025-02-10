package vn.clothing.store.utils

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import vn.clothing.store.MyApplication
import vn.clothing.store.R
import java.util.Base64
import kotlin.random.Random


object Utils {

    const val TAG = "Utils"

    @SuppressLint("DefaultLocale")
    fun generateOTP():String{
        val otp = Random.nextInt(0,1000000)
        return String.format("%06d", otp)
    }

    fun sendNotification(context: Context, title:String, text:String){
        val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        val notification = NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setLargeIcon(bitmap)
            .setColor(context.resources.getColor(R.color.colorPrimary))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }


    //Lấy FCM Token của ứng dụng
    fun getTokenFCM(callback:(String)->Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.w(TAG,"My Token: $token")
                callback(token)
            }
        }
    }

    fun generateBarcode(data: String): Bitmap? {
        return try {
            val writer = MultiFormatWriter()
            val bitMatrix: BitMatrix = writer.encode(data, BarcodeFormat.CODE_128, 512, 256) // Kiểu mã vạch CODE_128 và kích thước
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }

            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun calculateEAN13Checksum(data: String): Int {
        if (data.length != 12) {
            throw IllegalArgumentException("EAN-13 must be 12 digits long.")
        }

        var sum = 0
        for (i in data.indices) {
            val digit = Character.getNumericValue(data[i])
            if (i % 2 == 0) {  // Vị trí chẵn (index 0, 2, 4, 6, ...) nhân với 1
                sum += digit
            } else {  // Vị trí lẻ (index 1, 3, 5, 7, ...) nhân với 3
                sum += digit * 3
            }
        }

        val remainder = sum % 10
        return if (remainder == 0) 0 else 10 - remainder
    }


    fun generateEAN13WithChecksum(productId: String): String {
        if (productId.length != 12) {
            throw IllegalArgumentException("Product ID must be 12 digits long.")
        }

        // Calculate the checksum
        val checksum = calculateEAN13Checksum(productId)

        // Return the product ID with the checksum appended
        return productId + checksum
    }

    fun generateEAN13Barcode(text: String, width: Int, height: Int): Bitmap? {
        if (text.length != 12 && text.length != 13) {
            throw IllegalArgumentException("EAN-13 barcode must be 12 or 13 digits long. Provided length: ${text.length}")
        }
        return try {
            val writer = MultiFormatWriter()
            val bitMatrix: BitMatrix = writer.encode(text, BarcodeFormat.EAN_13, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    fun encodeToBase64(input: String): String {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(input.toByteArray())
        }else{
            android.util.Base64.encodeToString(input.toByteArray(), android.util.Base64.DEFAULT)
        }
    }

    fun decodeFromBase64(encoded: String): String {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String(Base64.getDecoder().decode(encoded))
        }else{
            android.util.Base64.decode(encoded, android.util.Base64.DEFAULT).toString(Charsets.UTF_8)
        }
    }

    fun generateQRCode(content: String, width: Int, height: Int): Bitmap? {
        try {
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix: BitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }


}