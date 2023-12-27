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

class RvMyVoucherAdapter(private val onClick:ClickObjectInterface<String>):RecyclerView.Adapter<RvMyVoucherAdapter.viewHolder>() {

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



    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            holder.idVoucher.text = list[position].id
            holder.tvContent.text = list[position].content
            holder.tvTitle.text = list[position].title

            holder.btnCopyCode.setOnClickListener {
                list[position].id?.let { it1 -> onClick.onClickListener(it1) }
            }

        }
    }


    override fun getItemCount(): Int {
       return list.size
    }
}