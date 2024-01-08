package com.example.clothingstoreapp.Adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.Model.FormatCurrency
import com.example.clothingstoreapp.Model.Notification
import com.example.clothingstoreapp.R
import com.google.android.material.imageview.ShapeableImageView

class RvNotificationAdapter(private val list:List<Notification>):RecyclerView.Adapter<RvNotificationAdapter.viewHolder>() {



    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var imgNotification:ShapeableImageView
        var title:TextView
        var content:TextView
        var timeSend:TextView
        var lnMainNotification:LinearLayout
        init {
            imgNotification = itemView.findViewById(R.id.imgNotification)
            title = itemView.findViewById(R.id.title)
            content = itemView.findViewById(R.id.content)
            timeSend = itemView.findViewById(R.id.timeSend)
            lnMainNotification = itemView.findViewById(R.id.lnMainNotification)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_notification,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context).load(list[position].img).into(holder.imgNotification)
            holder.title.text = list[position].title
            holder.content.text = list[position].content
            holder.timeSend.text = list[position].timeSend?.let {
                FormatCurrency.dateFormat.format(
                    it
                )
            }

            if(list[position].isRead == false){
                val color = ContextCompat.getColor(context, R.color.fourColor)
                ViewCompat.setBackgroundTintList(holder.lnMainNotification, ColorStateList.valueOf(color))
            }

            holder.itemView.setOnClickListener {
                list[position].isRead = true
                notifyItemChanged(position)
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //Câập nhật item đã đọc
}