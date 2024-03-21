package com.example.clothingstoreadmin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.model.ProductDetails

class RvAddColorsAdapter(
    private val onRemove: ClickObjectInterface<String>
) : RecyclerView.Adapter<RvAddColorsAdapter.viewHolder>() {
    private var list: List<ProductDetails> = listOf()


    @SuppressLint("NotifyDataSetChanged")
    fun setData(lst: List<ProductDetails>) {
        list = lst
        notifyDataSetChanged()
    }


    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNameColor: TextView
        var tvSize: TextView
        var tvQuantity: TextView

        var btnRemove: ImageView

        init {
            tvNameColor = itemView.findViewById(R.id.tvNameColor)
            tvSize = itemView.findViewById(R.id.tvSize)
            tvQuantity = itemView.findViewById(R.id.tvQuantity)

            btnRemove = itemView.findViewById(R.id.btnRemove)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_add_classify, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            holder.tvNameColor.text = list[position].nameClassify
            holder.tvSize.text = list[position].size
            holder.tvQuantity.text = list[position].quantity.toString()

            holder.btnRemove.setOnClickListener {
                list[position].id?.let { it1 -> onRemove.onClickListener(it1) }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}