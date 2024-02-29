package com.example.clothingstoreapp.Adapter

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
import com.example.clothingstoreapp.Interface.CkbObjectInterface
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.FormatCurrency
import com.example.clothingstoreapp.Model.ItemCart
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.R

class RvItemCartAdapter(private var list:List<ItemCart>, private val onClick:ClickObjectInterface<ItemCart>,
    private val onChecked:CkbObjectInterface<ItemCart>):RecyclerView.Adapter<RvItemCartAdapter.viewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<ItemCart>){
        this.list = list
        notifyDataSetChanged()
    }

    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var checkbox:CheckBox
        var imgProduct:ImageView
        var nameProduct:TextView
        var tvClassify:TextView
        var tvSize:TextView
        var tvPrice:TextView
        var tvQuantity:TextView
        var btnDown:ImageButton
        var btnUp:ImageButton
        init {
            checkbox = itemView.findViewById(R.id.checkbox)
            imgProduct = itemView.findViewById(R.id.imgProduct)
            nameProduct = itemView.findViewById(R.id.nameProduct)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            tvClassify = itemView.findViewById(R.id.tvClassify)
            tvSize = itemView.findViewById(R.id.tvSize)
            tvQuantity = itemView.findViewById(R.id.tvQuantity)
            btnDown = itemView.findViewById(R.id.btnDown)
            btnUp = itemView.findViewById(R.id.btnUp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_item_cart,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context).load(list[position].product?.imgPreview).into(holder.imgProduct)
            holder.nameProduct.text = list[position].product?.name ?: "ERROR!"
            holder.tvPrice.text = FormatCurrency.numberFormat.format(list[position].product?.price)

            holder.tvSize.text = resources.getString(R.string.size_cart, list[position].product?.classify)
            holder.tvQuantity.text = list[position].product?.quantity.toString()
            holder.tvClassify.text = resources.getString(R.string.str_classify, list[position].product?.color)

            holder.checkbox.setOnClickListener {
                if(holder.checkbox.isChecked){
                    onChecked.isChecked(list[position])
                }else{
                    onChecked.isNotChecked(list[position])
                }
            }

            holder.btnUp.setOnClickListener {
                upDownQuantity(holder.btnUp,holder,position)
            }

            holder.btnDown.setOnClickListener {
                upDownQuantity(holder.btnDown,holder,position)
            }
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    private fun upDownQuantity(view: ImageButton, holder: viewHolder, position: Int){
        var quantity = list[position].product?.quantity?:1

        if(view.id == R.id.btnUp){
            quantity += 1
        }else if(view.id == R.id.btnDown && quantity >1){
            quantity -=1
        }

        holder.tvQuantity.text = quantity.toString()

        list[position].product?.quantity = quantity

        onClick.onClickListener(list[position])
    }
}