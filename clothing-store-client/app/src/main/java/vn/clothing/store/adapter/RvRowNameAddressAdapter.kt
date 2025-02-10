package vn.clothing.store.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.Consumer
import androidx.recyclerview.widget.RecyclerView
import vn.clothing.store.R

//Use Pari (id,value),
class RvRowNameAddressAdapter(private var list:List<Pair<String,String>>?, private val onClick:Consumer<Pair<String,String>>) : RecyclerView.Adapter<RvRowNameAddressAdapter.ItemViewHolder>(){

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:List<Pair<String,String>>?){
        this.list = list
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.simple_text_layout,parent,false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if(!list.isNullOrEmpty()){
            holder.itemView.apply {
                findViewById<TextView>(R.id.text).text = list?.get(position)?.second
                setOnClickListener {
                    onClick.accept(Pair(list!![position].first, list!![position].second))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list?.size?:0
    }
}