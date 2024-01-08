package com.example.clothingstoreadmin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.model.FormatCurrency
import com.example.clothingstoreadmin.model.Product


class RvProductAdapter(private var list:List<Product>, private val onClick: ClickObjectInterface<Product>):RecyclerView.Adapter<RvProductAdapter.viewHolder>() {


    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Product>){
        this.list = list
        notifyDataSetChanged()
    }

    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
         var imageView:ImageView
         var tvName:TextView
         var tvStar:TextView
         var tvPrice:TextView

        init {
            imageView = itemView.findViewById(R.id.imageView)
            tvName = itemView.findViewById(R.id.tvName)
            tvStar = itemView.findViewById(R.id.tvStar)
            tvPrice = itemView.findViewById(R.id.tvPrice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.viewholder_product,parent,false)
        return  viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context).load(list[position].img_preview?.get(0)).into(holder.imageView)
            holder.tvName.text = list[position].name

            holder.tvPrice.text = FormatCurrency.numberFormat.format(list[position].price)
            holder.tvStar.text= list[position].rateEvaluate.toString()
            holder.itemView.setOnClickListener {
                onClick.onClickListener(list[position])
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}