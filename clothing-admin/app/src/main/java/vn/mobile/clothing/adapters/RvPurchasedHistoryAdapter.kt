package vn.mobile.clothing.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.mobile.clothing.R
import vn.mobile.clothing.databinding.ViewholderPurchasedHistoryBinding
import vn.mobile.clothing.network.response.OrderResponseModel
import vn.mobile.clothing.utils.FormatCurrency


class RvPurchasedHistoryAdapter(private val onClick:Consumer<OrderResponseModel>) : RecyclerView.Adapter<RvPurchasedHistoryAdapter.ItemViewHolder>(){

    private var list = mutableListOf<OrderResponseModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:List<OrderResponseModel>){
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
        Glide.with(holder.itemView.context).load(list[position].iamge).into(holder.binding.imgProduct)
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