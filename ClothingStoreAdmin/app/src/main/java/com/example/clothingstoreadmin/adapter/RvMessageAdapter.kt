package com.example.clothingstoreadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.model.MessageModel
import com.example.clothingstoreadmin.model.UserManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class RvMessageAdapter(private val context: Context,options: FirestoreRecyclerOptions<MessageModel>) :
    FirestoreRecyclerAdapter<MessageModel, RvMessageAdapter.viewHolder>(options) {


    class viewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var ln_left_layout:LinearLayout
        var ln_right_layout:LinearLayout
        var tvLeftText:TextView
        var tvRightText:TextView
        init {
            ln_left_layout = itemView.findViewById(R.id.ln_left_layout)
            ln_right_layout = itemView.findViewById(R.id.ln_right_layout)
            tvLeftText = itemView.findViewById(R.id.tvLeftText)
            tvRightText = itemView.findViewById(R.id.tvRightText)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_message,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int, model: MessageModel) {
        if (model.senderId.equals(UserManager.getInstance().getUserID())) {
            holder.ln_left_layout.visibility = View.GONE
            holder.ln_right_layout.visibility = View.VISIBLE
            holder.tvRightText.text = model.message
        } else {
            holder.ln_right_layout.visibility = View.GONE
            holder.ln_left_layout.visibility = View.VISIBLE
            holder.tvLeftText.text = model.message
        }
    }
}