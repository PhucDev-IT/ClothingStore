package com.example.clothingstoreadmin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.model.CustomProduct
import com.example.clothingstoreadmin.model.FormatCurrency
import com.example.clothingstoreadmin.model.ItemCart


class RvCheckoutAdapter(private val list: List<ItemCart>): RecyclerView.Adapter<RvCheckoutAdapter.viewHolder>() {
    class viewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){

        var imgProduct: ImageView
        var nameProduct: TextView
        var tvClassify: TextView
        var tvSize: TextView
        var tvPrice: TextView
        var tvQuantity: TextView

        init {

            imgProduct = itemView.findViewById(R.id.imgProduct)
            nameProduct = itemView.findViewById(R.id.nameProduct)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            tvClassify = itemView.findViewById(R.id.tvClassify)
            tvSize = itemView.findViewById(R.id.tvSize)
            tvQuantity = itemView.findViewById(R.id.tvQuantity)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_item_product_checkout,parent,false)
        return viewHolder(view)
    }

    @SuppressLint("SetTextI18n", "StringFormatMatches")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context).load(list[position].image).into(holder.imgProduct)
            holder.nameProduct.text = list[position].name ?: "ERROR!"
            holder.tvPrice.text = FormatCurrency.numberFormat.format(list[position].price)

            holder.tvSize.text = resources.getString(R.string.size_cart, list[position].productDetail?.size)
            holder.tvQuantity.text = "x${list[position].quantity}"
            holder.tvClassify.text = resources.getString(R.string.str_classify, list[position].productDetail?.nameClassify)
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}