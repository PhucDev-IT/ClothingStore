package com.example.clothingstoreadmin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.model.FormatCurrency
import com.example.clothingstoreadmin.model.OrderModel


class RvOrderAdapter( private val onClick: ClickObjectInterface<OrderModel>):RecyclerView.Adapter<RvOrderAdapter.viewHolder>() {
    private var list: MutableList<OrderModel> = mutableListOf()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:List<OrderModel>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun updateData(list:List<OrderModel>){
        this.list.addAll(list)
        notifyItemChanged((this.list.size - list.size)-1)
    }

    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var imgProduct:ImageView
        var nameProduct:TextView
        var tvPrice:TextView
        var tvMore:TextView
        var tvDateOrder:TextView
        var tvQuantity:TextView
        var tvTotalMoney:TextView

        init {
            imgProduct = itemView.findViewById(R.id.imgProduct)
            nameProduct = itemView.findViewById(R.id.nameProduct)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            tvMore = itemView.findViewById(R.id.tvMore)
            tvDateOrder = itemView.findViewById(R.id.tvDateOrder)
            tvQuantity = itemView.findViewById(R.id.tvQuantity)
            tvTotalMoney = itemView.findViewById(R.id.tvTotalMoney)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_order,parent,false)

        return viewHolder(view)
    }

    @SuppressLint("CheckResult", "StringFormatMatches")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context).load(list[position].products?.get(0)?.imgPreview).into(holder.imgProduct)
            holder.nameProduct.text = list[position].products?.get(0)?.name
            holder.tvTotalMoney.text = FormatCurrency.numberFormat.format(list[position].totalMoney)
            holder.tvDateOrder.text = list[position].orderDate.let {
                FormatCurrency.dateFormat.format(it)
            }
            holder.tvQuantity.text = resources.getString(R.string.str_quantity,
                list[position].products?.get(0)?.quantity ?: 1
            )
            holder.tvMore.setOnClickListener{
                onClick.onClickListener(list[position])
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}