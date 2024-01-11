package com.example.clothingstoreadmin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.model.ChatRoomModel
import com.example.clothingstoreadmin.model.FormatCurrency
import com.example.clothingstoreadmin.model.MessageModel
import com.example.clothingstoreadmin.model.UserManager
import com.google.android.material.imageview.ShapeableImageView

class RvRecentChatAdapter(private var list:List<ChatRoomModel>, private val onClick:ClickObjectInterface<ChatRoomModel>) : RecyclerView.Adapter<RvRecentChatAdapter.viewHolder>(){

    private val idCurrentUser:String = UserManager.getInstance().getUserID()!!

    @SuppressLint("NotifyDataSetChanged")
    fun setData(lst:List<ChatRoomModel>){
        list = lst
        notifyDataSetChanged()
    }

    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var imgAvatar:ShapeableImageView
        var tvFullName:TextView
        var tvLastMessage:TextView
        var tvTimeSend:TextView

        init {
            tvTimeSend = itemView.findViewById(R.id.tvTimeSend)
            imgAvatar = itemView.findViewById(R.id.imgAvatar)
            tvFullName = itemView.findViewById(R.id.tvFullName)
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_recent_chat,parent,false)
        return viewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {

            holder.tvLastMessage.text = list[position].lastMessage
            holder.tvTimeSend.text = FormatCurrency.timeFormat.format(list[position].lastMessageTimestamp!!.toDate())

            if(!list[position].lastMessageSenderId.equals(idCurrentUser)){
                Glide.with(context).load(list[position].senderBy?.avatar).into(holder.imgAvatar)
                holder.tvFullName.text = list[position].senderBy?.fullName
            }else{
                holder.tvLastMessage.text = "Bạn: "+list[position].lastMessage
            }


            holder.itemView.setOnClickListener {
                onClick.onClickListener(list[position])
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}