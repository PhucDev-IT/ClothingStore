package com.example.clothingstoreadmin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import android.util.Log
import android.content.ContentValues.TAG
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.model.Voucher


class RvVoucherAdapter(private val onClick: ClickObjectInterface<Voucher>,private val onClickCopy: ClickObjectInterface<String>):RecyclerView.Adapter<RvVoucherAdapter.viewHolder>() {

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
        var btnEdit:ImageButton

        init {
            idVoucher = itemView.findViewById(R.id.idVoucher)
            tvContent = itemView.findViewById(R.id.tvContent)
            tvTitle = itemView.findViewById(R.id.tvTitle)
            btnCopyCode = itemView.findViewById(R.id.btnCopyCode)
            btnEdit = itemView.findViewById(R.id.btnEdit)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_voucher,parent,false)
        return viewHolder(view)
    }



    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            holder.idVoucher.text = list[position].id
            holder.tvContent.text = list[position].content
            holder.tvTitle.text = list[position].title

            holder.btnCopyCode.setOnClickListener {
                list[position].id?.let { it1 -> onClickCopy.onClickListener(it1) }
            }

            holder.btnEdit.setOnClickListener {
                onClick.onClickListener(list[position])
            }

        }
    }


    override fun getItemCount(): Int {
       return list.size
    }
}