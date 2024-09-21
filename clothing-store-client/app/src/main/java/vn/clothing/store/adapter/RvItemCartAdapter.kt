package vn.clothing.store.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.util.Consumer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.clothing.store.R
import vn.clothing.store.models.CartModel
import vn.clothing.store.networks.response.CartResponseModel
import vn.clothing.store.networks.response.CartResponseModel.CartItemResponseModel
import vn.clothing.store.utils.FormatCurrency


class RvItemCartAdapter(private var list:List<CartItemResponseModel>, private val onClick:Consumer<CartItemResponseModel>,
                        private val onChecked:Consumer<CartItemResponseModel>):RecyclerView.Adapter<RvItemCartAdapter.viewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<CartItemResponseModel>?){
        if(list.isNullOrEmpty()){
            this.list = list!!
            notifyDataSetChanged()
        }
    }

    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var checkbox:CheckBox
        var imgProduct:ImageView
        var nameProduct:TextView
        var tvColor:TextView
        var tvSize:TextView
        var tvPrice:TextView
        var tvQuantity:TextView
        var btnDown:ImageButton
        var btnUp:ImageButton
        init {
            checkbox = itemView.findViewById(R.id.checkbox)
            imgProduct = itemView.findViewById(R.id.imgProduct)
            nameProduct = itemView.findViewById(R.id.nameProduct)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            tvColor = itemView.findViewById(R.id.tv_color)
            tvSize = itemView.findViewById(R.id.tvSize)
            tvQuantity = itemView.findViewById(R.id.tvQuantity)
            btnDown = itemView.findViewById(R.id.btnDown)
            btnUp = itemView.findViewById(R.id.btnUp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_item_cart,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context).load(list[position].image).into(holder.imgProduct)
            holder.nameProduct.text = list[position].name ?: "ERROR!"
            holder.tvPrice.text = FormatCurrency.numberFormat.format(list[position].price)

            holder.tvSize.text = resources.getString(R.string.label_size,
                list[position].size
            )
            holder.tvQuantity.text = list[position].quantity.toString()
            holder.tvColor.text = resources.getString(R.string.label_color,
                list[position].color
            )

            holder.checkbox.setOnClickListener {
                if(holder.checkbox.isChecked){
                    onChecked.accept(list[position])
                }else{
                    onChecked.accept(list[position])
                }
            }

            holder.btnUp.setOnClickListener {

            }

            holder.btnDown.setOnClickListener {

            }
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

}