package vn.mobile.clothing.models

import android.content.Context
import vn.mobile.clothing.R

enum class EOrderStatus(val display: Int) {
    PENDING(R.string.tab_item_pending),
    PACKING(R.string.tab_item_processing),
    SHIPPING(R.string.tab_item_shipping),
    DELIVERED(R.string.success),
    CANCELLED(R.string.cancel);

    fun getDisplayName(context: Context): String {
        return context.getString(display)
    }
    companion object{
        fun findName(name:String, context: Context): String {
            return when(name){
                PENDING.name -> PENDING.getDisplayName(context)
                PACKING.name -> PACKING.getDisplayName(context)
                SHIPPING.name -> SHIPPING.getDisplayName(context)
                DELIVERED.name -> DELIVERED.getDisplayName(context)
                CANCELLED.name -> CANCELLED.getDisplayName(context)
                else -> ""
            }
        }
    }
}
