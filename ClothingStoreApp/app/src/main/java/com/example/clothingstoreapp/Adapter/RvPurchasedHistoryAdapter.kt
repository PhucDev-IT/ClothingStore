package com.example.clothingstoreapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.FormatCurrency
import com.example.clothingstoreapp.Model.OrderModel
import com.example.clothingstoreapp.R

class RvPurchasedHistoryAdapter(private var list: List<OrderModel>, private val onClick:ClickObjectInterface<OrderModel>):RecyclerView.Adapter<RvPurchasedHistoryAdapter.viewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:List<OrderModel>){
        this.list  = list
        notifyDataSetChanged()
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_purchased_history,parent,false)

        return viewHolder(view)
    }

    @SuppressLint("CheckResult", "StringFormatMatches")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context).load(list[position].carts?.get(0)?.product?.img_preview?.get(0)).into(holder.imgProduct)
            holder.nameProduct.text = list[position].carts?.get(0)?.product?.name
            holder.tvTotalMoney.text = FormatCurrency.numberFormat.format(list[position].totalMoney)
            holder.tvDateOrder.text = list[position].orderDate?.let {
                FormatCurrency.dateFormat.format(it)
            }
            holder.tvQuantity.text = resources.getString(R.string.str_quantity,
                list[position].carts?.get(0)?.quantity ?: 1
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