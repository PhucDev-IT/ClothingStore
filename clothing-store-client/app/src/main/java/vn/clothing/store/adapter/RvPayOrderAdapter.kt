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
import vn.clothing.store.networks.response.CartResponseModel
import vn.clothing.store.utils.FormatCurrency

class RvPayOrderAdapter(private val list: List<CartResponseModel.CartItemResponseModel>) :
    RecyclerView.Adapter<RvPayOrderAdapter.viewHolder>() {
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgProduct: ImageView
        var nameProduct: TextView
        var tvColor: TextView
        var tvSize: TextView
        var tvPrice: TextView
        var tvQuantity: TextView


        init {
            imgProduct = itemView.findViewById(R.id.imgProduct)
            nameProduct = itemView.findViewById(R.id.nameProduct)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            tvColor = itemView.findViewById(R.id.tv_color)
            tvSize = itemView.findViewById(R.id.tvSize)
            tvQuantity = itemView.findViewById(R.id.tv_quantity)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_in_pay_order, parent, false)
        return viewHolder(view)
    }

    /*
    *We need to check if the item is selected or not
    * If item is checked , we transmit item with state = true
    * Wit if user want to up quantity, we transmit item with state = true
     */

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context).load(list[position].image).into(holder.imgProduct)
            holder.nameProduct.text = list[position].name ?: "ERROR!"
            holder.tvPrice.text = FormatCurrency.numberFormat.format(list[position].price!! * list[position].quantity!!)

            holder.tvSize.text = resources.getString(
                R.string.label_size,
                list[position].size
            )
            holder.tvColor.text = resources.getString(
                R.string.label_color,
                list[position].color
            )
            holder.tvQuantity.text = "x${list[position].quantity.toString()}"

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}