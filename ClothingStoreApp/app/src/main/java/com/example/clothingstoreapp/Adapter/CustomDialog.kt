package com.example.clothingstoreapp.Adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView

import com.example.clothingstoreapp.R

class CustomDialog(private val context: Context) {

    private lateinit var dialog: Dialog

    fun dialogLoadingBasic(title:String?){
        dialog = Dialog(context, R.style.CustomAlert)
        dialog.setContentView(R.layout.custom_loading_dialog)

        val textView = dialog.findViewById<TextView>(R.id.title)
        textView.text = title?:"Đang xử lý ...."

        dialog.setCancelable(false)
        dialog.show()
    }

    //Title, Content, 2 button
    fun dialogBasic(title: String,content:String,callback:(Boolean)->Unit) {
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_basic)

        val window: Window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute: WindowManager.LayoutParams = window.attributes
        windowAttribute.gravity = Gravity.BOTTOM
        window.attributes = windowAttribute

        val head = dialog.findViewById<TextView>(R.id.tvTitle)
        val body = dialog.findViewById<TextView>(R.id.tvContent)
        val btnNo = dialog.findViewById<TextView>(R.id.btnNo)
        val btnOk = dialog.findViewById<TextView>(R.id.btnOk)

        head.text  = title
        body.text = content

        btnNo.setOnClickListener {
            closeDialog()
            callback(false)
        }

        btnOk.setOnClickListener {
            closeDialog()
            callback(true)
        }

        dialog.show()
    }


    fun closeDialog(){
        if(dialog!=null)
            dialog.dismiss()
    }
}