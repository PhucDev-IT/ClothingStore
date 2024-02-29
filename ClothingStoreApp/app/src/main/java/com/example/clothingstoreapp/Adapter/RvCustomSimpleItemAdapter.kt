package com.example.clothingstoreapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.Interface.CkbObjectInterface
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.R

class RvCustomSimpleItemAdapter(private var list: List<String>?,private val onClick:ClickObjectInterface<String>): RecyclerView.Adapter<RvCustomSimpleItemAdapter.viewHolder>(){

    @SuppressLint("NotifyDataSetChanged")
    fun setData(lst:List<String>?){
        this.list = lst
        notifyDataSetChanged()
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName: TextView
        init {
            tvName = itemView.findViewById(R.id.text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.simple_recyclerview_item,parent,false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            if(list!=null){
                holder.tvName.text = list!![position]

                holder.itemView.setOnClickListener {
                    onClick.onClickListener(list!![position])
                }
            }
        }
    }


}