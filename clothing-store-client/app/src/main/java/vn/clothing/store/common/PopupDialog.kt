package vn.clothing.store.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.util.Consumer
import com.bumptech.glide.Glide
import vn.clothing.store.R
import vn.clothing.store.databinding.LayoutPopupBinding


object PopupDialog {

    enum class PopupType{
        NOTIFICATION, CONFIRM
    }

    private var dialog: Dialog?=null


    fun showDialogLoading(context: Context){
        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.custom_loading_dialog)

        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    fun showDialog(context: Context,type:PopupType?,title:String?,body:String,callback:Consumer<Boolean>){
        if(dialog?.isShowing == true) closeDialog()
        dialog = Dialog(context, R.style.Theme_Dialog)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        val binding = LayoutPopupBinding.inflate(LayoutInflater.from(context))
        dialog!!.setContentView(binding.root)

        if (type == PopupType.NOTIFICATION) {
            binding.btnCancel.visibility = View.GONE
        }

        binding.tvBody.text = body
        binding.tvTitle.text = title?:context.getString(R.string.app_name)

        binding.btnCancel.setOnClickListener { v: View? ->
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
        binding.btnOk.setOnClickListener { v: View? ->
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            callback.accept(true)
        }

        if(dialog !=null){
            dialog!!.show()
        }
    }

    fun closeDialog(){
        if(dialog !=null){
            dialog!!.dismiss()
        }
    }

}