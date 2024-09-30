package vn.clothing.store.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import android.util.Log
import android.content.ContentValues.TAG
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import vn.clothing.store.R
import vn.clothing.store.models.VoucherModel

class RvMyVoucherAdapter(private val onClick:Consumer<String>):RecyclerView.Adapter<RvMyVoucherAdapter.viewHolder>() {

    private var list:List<VoucherModel> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(lst:List<VoucherModel>){
        list = lst.toMutableList()
        notifyDataSetChanged()
    }


    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var idVoucher:TextView
        var tvContent:TextView
        var tvTitle:TextView
        var btnSelect:AppCompatButton

        init {
            idVoucher = itemView.findViewById(R.id.idVoucher)
            tvContent = itemView.findViewById(R.id.tvContent)
            tvTitle = itemView.findViewById(R.id.tvTitle)
            btnSelect = itemView.findViewById(R.id.btn_select)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_voucher,parent,false)
        return viewHolder(view)
    }



    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            holder.idVoucher.text = list[position].id
            holder.tvContent.text = list[position].description
            holder.tvTitle.text = list[position].title

            holder.btnSelect.setOnClickListener {
                onClick.accept(list[position].id!!)
            }

        }
    }


    override fun getItemCount(): Int {
       return list.size
    }
}