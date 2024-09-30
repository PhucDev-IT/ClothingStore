package vn.clothing.store.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.clothing.store.R
import vn.clothing.store.models.OrderItem
import vn.clothing.store.networks.response.OrderItemResponseModel
import vn.clothing.store.utils.FormatCurrency


class RvCheckoutAdapter(private val list: List<OrderItemResponseModel>): RecyclerView.Adapter<RvCheckoutAdapter.viewHolder>() {
    class viewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){

        var imgProduct: ImageView
        var nameProduct: TextView
        var tvClassify: TextView
        var tvSize: TextView
        var tvPrice: TextView
        var tvQuantity: TextView

        init {

            imgProduct = itemView.findViewById(R.id.imgProduct)
            nameProduct = itemView.findViewById(R.id.nameProduct)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            tvClassify = itemView.findViewById(R.id.tvClassify)
            tvSize = itemView.findViewById(R.id.tvSize)
            tvQuantity = itemView.findViewById(R.id.tvQuantity)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_item_product_checkout,parent,false)
        return viewHolder(view)
    }

    @SuppressLint("SetTextI18n", "StringFormatMatches")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context).load(list[position].image).into(holder.imgProduct)
            holder.nameProduct.text = list[position].productName ?: "ERROR!"
            holder.tvPrice.text = FormatCurrency.numberFormat.format(list[position].price)

            holder.tvSize.text = resources.getString(R.string.label_size, list[position].size)
            holder.tvQuantity.text = "x${list[position].quantity}"
            holder.tvClassify.text = resources.getString(R.string.label_color, list[position].color)
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}