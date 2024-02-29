package com.example.clothingstoreapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.AddressModel
import com.example.clothingstoreapp.R

class RvOptionsAddressAdapter(private val list: List<AddressModel>, private val onClick:ClickObjectInterface<AddressModel>):RecyclerView.Adapter<RvOptionsAddressAdapter.viewHolder>() {
    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var tvTypeAddress:TextView
        var tvFullName:TextView
        var tvNumberPhone:TextView
        var tvDetailsAddress:TextView
        var tvInforAddress:TextView
        var rdoButton:RadioButton

        init {
            tvTypeAddress = itemView.findViewById(R.id.tvTypeAddress)
            tvFullName = itemView.findViewById(R.id.tvFullName)
            tvNumberPhone = itemView.findViewById(R.id.tvNumberPhone)
            tvDetailsAddress = itemView.findViewById(R.id.tvDetailsAddress)
            tvInforAddress = itemView.findViewById(R.id.tvInforAddress)
            rdoButton = itemView.findViewById(R.id.rdoButton)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_address,parent,false)
        return viewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            holder.tvTypeAddress.text = list[position].typeAddress
            holder.tvFullName.text = list[position].fullName
            holder.tvNumberPhone.text = list[position].numberPhone
            holder.tvDetailsAddress.text = list[position].addressDetails

            holder.tvInforAddress.text = "${list[position].province?.ProvinceName}, ${list[position].district?.districtName}, ${list[position].ward?.WardName}"

            holder.rdoButton.setOnClickListener {
                onClick.onClickListener(list[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}