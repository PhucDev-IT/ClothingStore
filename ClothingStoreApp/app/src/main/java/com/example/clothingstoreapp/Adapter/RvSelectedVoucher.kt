package com.example.clothingstoreapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.Model.Voucher
import com.example.clothingstoreapp.R
import android.util.Log
import android.content.ContentValues.TAG
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.clothingstoreapp.Interface.CkbObjectInterface
import com.example.clothingstoreapp.Interface.ClickObjectInterface

class RvSelectedVoucher(private val onClick:CkbObjectInterface<Voucher>,private var idVoucherOld:String?):RecyclerView.Adapter<RvSelectedVoucher.viewHolder>() {

    private var list:List<Voucher> = ArrayList()



    @SuppressLint("NotifyDataSetChanged")
    fun setData(lst:List<Voucher>){
        list = lst.toMutableList()
        notifyDataSetChanged()
    }


    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var idVoucher:TextView
        var tvContent:TextView
        var tvTitle:TextView
        var btnCopyCode:AppCompatButton

        init {
            idVoucher = itemView.findViewById(R.id.idVoucher)
            tvContent = itemView.findViewById(R.id.tvContent)
            tvTitle = itemView.findViewById(R.id.tvTitle)
            btnCopyCode = itemView.findViewById(R.id.btnCopyCode)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_voucher,parent,false)
        return viewHolder(view)
    }


    var pos: Int = -1
    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            holder.idVoucher.text = list[position].id
            holder.tvContent.text = list[position].content
            holder.tvTitle.text = list[position].title
            holder.btnCopyCode.text = "Sử dụng"

            if(idVoucherOld == list[position].id){
                idVoucherOld  = null
                pos =  position
            }

            holder.btnCopyCode.setOnClickListener {
                val previousSelected = pos
                if (pos != position) {
                    pos = position
                    onClick.isChecked(list[position])
                }else{
                   pos =  -1
                    onClick.isNotChecked(list[position])
                }

                notifyItemChanged(previousSelected) // Cập nhật ViewHolder trước đó nếu có
                notifyItemChanged(position) // Cập nhật ViewHolder đang được chọn
            }

            if(pos == position){
                holder.btnCopyCode.text = "Bỏ chọn"
                holder.btnCopyCode.setBackgroundResource(R.drawable.bg_btn_bottom_radius_primary)
                holder.btnCopyCode.setTextColor(ContextCompat.getColor(context,R.color.white))

            }else{
                holder.btnCopyCode.text = "Sử dụng"
                holder.btnCopyCode.setBackgroundResource(R.drawable.bg_btn_bottom_radius_placeholder)
                holder.btnCopyCode.setTextColor(ContextCompat.getColor(context,R.color.primarykeyColor))
            }

        }
    }


    override fun getItemCount(): Int {
       return list.size
    }
}