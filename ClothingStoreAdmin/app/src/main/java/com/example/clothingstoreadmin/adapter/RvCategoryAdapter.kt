package com.example.clothingstoreadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreadmin.Interface.CkbObjectInterface
import com.example.clothingstoreadmin.R
import com.example.clothingstoreadmin.model.Category


class RvCategoryAdapter(private val list:List<Category>, private val onclick:CkbObjectInterface<String>):RecyclerView.Adapter<RvCategoryAdapter.viewHolder>() {

    class viewHolder(item:View):RecyclerView.ViewHolder(item){
        val checkbox : CheckBox
        init {
            checkbox = item.findViewById(R.id.checkbox)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
       holder.itemView.apply {
           holder.checkbox.text = list[position].nameCategory

           holder.checkbox.setOnClickListener {
               if(holder.checkbox.isChecked){
                   list[position].id?.let { it1 -> onclick.isChecked(it1) }
               }else{
                   list[position].id?.let { it1 -> onclick.isChecked(it1) }
               }
           }
       }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}