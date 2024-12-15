package vn.clothing.store.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.clothing.store.R
import vn.clothing.store.databinding.ViewholderPurchasedHistoryBinding
import vn.clothing.store.models.Order
import vn.clothing.store.networks.response.OrderResponseModel
import vn.clothing.store.networks.response.PreviewOrderResponseModel
import vn.clothing.store.utils.FormatCurrency

class RvPurchasedHistoryAdapter(private val onClick:Consumer<PreviewOrderResponseModel>) : RecyclerView.Adapter<RvPurchasedHistoryAdapter.ItemViewHolder>(){

    private var list = mutableListOf<PreviewOrderResponseModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:List<PreviewOrderResponseModel>){
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData(){
        list.clear()
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val binding: ViewholderPurchasedHistoryBinding = ViewholderPurchasedHistoryBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_purchased_history,parent,false)
    return ItemViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(list[position].image).into(holder.binding.imgProduct)
        holder.binding.nameProduct.text = list[position].productName
        holder.binding.tvTotalProduct.text = list[position].itemCount.toString() + " mặt hàng"
        holder.binding.tvDateOrder.text =  FormatCurrency.dateFormat.format(list[position].orderDate)
        holder.binding.tvTotalMoney.text = FormatCurrency.numberFormat.format(list[position].realTotal)

        holder.itemView.setOnClickListener {
            onClick.accept(list[position])

        }
    }

    override fun getItemCount(): Int {
       return list.size
    }
}