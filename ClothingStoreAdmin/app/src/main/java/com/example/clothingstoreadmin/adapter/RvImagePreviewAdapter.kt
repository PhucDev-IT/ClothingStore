package com.example.clothingstoreadmin.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.R


class RvImagePreviewAdapter(private var list: List<String>, private val onClick: ClickObjectInterface<String>) : RecyclerView.Adapter<RvImagePreviewAdapter.viewHolder>(){
    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(lst:List<String>){
        list = lst
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.img_preview_product,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            val imgPreview = findViewById<ImageView>(R.id.imgPreview)

            Glide.with(context).load(list[position]).into(imgPreview)

            holder.itemView.setOnClickListener {
                list[position].let { it1 -> onClick.onClickListener(it1) }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size ?:0
    }
}