package vn.clothing.store.common

import android.content.Context
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import vn.clothing.store.R
import vn.clothing.store.databinding.CustomLayoutToastBinding


object CoreConstant {
    enum class ToastType {
        SUCCESS,
        ERROR,
        WARNING,
        INFO
    }

    private var duration: Int = Toast.LENGTH_SHORT
    private var gravity: Int = Gravity.TOP or Gravity.CENTER_HORIZONTAL


    fun showToast(context: Context, message: String, type: ToastType) {
        val toast = Toast(context)
        val binding = CustomLayoutToastBinding.inflate(LayoutInflater.from(context))
        binding.message.text = message
        toast.duration = duration
        toast.setGravity(gravity,0,50)
        toast.view = binding.root
        when(type){
            ToastType.SUCCESS -> success(context,binding.llContainer,binding.icon)
            ToastType.ERROR -> error(context,binding.llContainer,binding.icon)
            ToastType.WARNING -> warning(context,binding.llContainer,binding.icon)
            ToastType.INFO -> info(context,binding.llContainer,binding.icon)
        }
        toast.show()
    }


    private fun success(context:Context, container: LinearLayout, icon: ImageView){
        val color = ContextCompat.getColor(context, R.color.success)
        container.backgroundTintList = ColorStateList.valueOf(color)
        icon.setImageResource(R.drawable.baseline_check_24)
    }


    private fun warning(context:Context, container: LinearLayout, icon: ImageView){
        val color = ContextCompat.getColor(context, R.color.warning)
        container.backgroundTintList = ColorStateList.valueOf(color)
     //   icon.setImageResource(R.drawable.icon_warning)
    }

    private fun error(context:Context, container: LinearLayout, icon: ImageView){
        val color = ContextCompat.getColor(context, R.color.failed)
        container.backgroundTintList = ColorStateList.valueOf(color)
        icon.setImageResource(R.drawable.baseline_cancel_24)
    }

    private fun info(context:Context, container: LinearLayout, icon: ImageView){
        val color = ContextCompat.getColor(context, R.color.info)
        container.backgroundTintList = ColorStateList.valueOf(color)
        icon.setImageResource(R.drawable.baseline_info_24)
    }

    /**
     * Duration must be 0 or 1
     * 0: Toast.LENGTH_SHORT
     * 1: Toast.LENGTH_LONG
     */
    fun setDuration(duration: Int) {
        if(duration<0 || duration >1){
            throw IllegalArgumentException("Duration must be 0 or 1")
        }
        this.duration = duration
    }

    fun setGravity(gravity: Int) {
        this.gravity = gravity
    }

}