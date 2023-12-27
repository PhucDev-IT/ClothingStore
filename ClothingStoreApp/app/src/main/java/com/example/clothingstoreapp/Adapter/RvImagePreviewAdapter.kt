package com.example.clothingstoreapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.R

class RvImagePreviewAdapter(private val list: List<String>, private val onClick:ClickObjectInterface<String>) : RecyclerView.Adapter<RvImagePreviewAdapter.viewHolder>(){
    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.img_preview_product,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            val imgPreview = findViewById<ImageView>(R.id.imgPreview)

            Glide.with(context).load(list[position]).into(imgPreview)

            holder.itemView.setOnClickListener {
                onClick.onClickListener(list[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}