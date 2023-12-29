package com.example.clothingstoreapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.Model.FormatCurrency
import com.example.clothingstoreapp.Model.ItemCart
import com.example.clothingstoreapp.R

class RvCheckoutAdapter(private val list: List<ItemCart>?): RecyclerView.Adapter<RvCheckoutAdapter.viewHolder>() {
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
           if(list != null){
               Glide.with(context).load(list[position].product?.imgPreview).into(holder.imgProduct)
               holder.nameProduct.text = list[position].product?.name ?: "ERROR!"
               holder.tvPrice.text = FormatCurrency.numberFormat.format(list[position].product?.price)

               holder.tvSize.text = resources.getString(R.string.size_cart, list[position].classify)
               holder.tvQuantity.text = "x${list[position].quantity}"
               holder.tvClassify.text = resources.getString(R.string.str_classify, list[position].color)
           }
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}