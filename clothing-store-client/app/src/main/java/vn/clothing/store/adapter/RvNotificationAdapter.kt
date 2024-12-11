package vn.clothing.store.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.clothing.store.R
import vn.clothing.store.databinding.ViewhoderNotificationBinding
import vn.clothing.store.models.NotificationModel
import vn.clothing.store.utils.FormatCurrency

class RvNotificationAdapter( private val onClick:Consumer<NotificationModel>): RecyclerView.Adapter<RvNotificationAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
         val binding = ViewhoderNotificationBinding.bind(itemView)
    }
    private var unReads = mutableListOf<String>()
    private var list:MutableList<NotificationModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:List<NotificationModel>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun markAll(){
        this.list.forEach {
            it.isRead = true
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(notification:NotificationModel){
        list.remove(notification)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewhoder_notification,parent,false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.tvTitle.text = list[position].title
        holder.binding.tvContent.text = list[position].content
        holder.binding.tvTime.text = FormatCurrency.dateFormat.format(list[position].date)
        Glide.with(holder.itemView.context).load(list[position].type).into(holder.binding.icon)
        if(list[position].isRead == true){
            holder.binding.viewRead.visibility = View.GONE
        }else{
            unReads.add(list[position].id)
            holder.binding.viewRead.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            if(list[position].isRead == false){
                holder.binding.viewRead.visibility = View.GONE
                unReads.remove(list[position].id)
                onClick.accept(list[position])
            }
        }
    }

    fun getUnReads():List<String>{
        return unReads
    }

    override fun getItemCount(): Int {
       return list.size
    }
}