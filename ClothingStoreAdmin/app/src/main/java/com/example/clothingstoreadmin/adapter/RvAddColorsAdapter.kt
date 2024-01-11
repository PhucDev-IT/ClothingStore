package com.example.clothingstoreadmin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreadmin.Interface.ClickObjectInterface
import com.example.clothingstoreadmin.R
import com.nvt.color.ColorPickerDialog

class RvAddColorsAdapter(private val onChange:ClickObjectInterface<Map<String,Int>>,private val onRemove:ClickObjectInterface<String>):RecyclerView.Adapter<RvAddColorsAdapter.viewHolder>() {
    private var list: MutableMap<String, Int> = mutableMapOf()


    @SuppressLint("NotifyDataSetChanged")
    fun setData(lst:MutableMap<String,Int>){
        list = lst
        notifyDataSetChanged()
    }


    fun insertData(name:String, color:Int?){
        list[name] = color?: 0
        notifyItemChanged(list.size-1)
    }

    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var tvNameColor:TextView
        var viewColor:View
        var btnRemove:ImageView
        init {
            tvNameColor = itemView.findViewById(R.id.tvNameColor)
            viewColor = itemView.findViewById(R.id.viewColor)
            btnRemove = itemView.findViewById(R.id.btnRemove)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_add_colors,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            holder.tvNameColor.text = list.keys.elementAt(position)

            if(list.values.elementAt(position)==0){
                holder.viewColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
            }else{
                holder.viewColor.backgroundTintList = ColorStateList.valueOf(list.values.elementAt(position))
            }

            holder.viewColor.setOnClickListener {
                changeColor(context,list.keys.elementAt(position),position)
            }

            holder.btnRemove.setOnClickListener {
                onRemove.onClickListener(list.keys.elementAt(position))
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun changeColor(context: Context,key:String,position: Int){
        val colorPicker = ColorPickerDialog(
            context,
            Color.BLACK, // color init
            true, // true is show alpha
            object : ColorPickerDialog.OnColorPickerListener {
                override fun onCancel(dialog: ColorPickerDialog?) {
                    // handle click button Cancel
                }

                override fun onOk(dialog: ColorPickerDialog?, colorPicker: Int) {
                    list[key] = colorPicker
                    onChange.onClickListener(hashMapOf(key to colorPicker))
                    notifyItemChanged(position)
                }
            })
        colorPicker.show()
    }
}