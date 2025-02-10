package vn.mobile.clothing.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import vn.mobile.clothing.R
import vn.mobile.clothing.network.response.TopProductResponseModel

class RvTopProductAdapter(private var list: List<TopProductResponseModel>): RecyclerView.Adapter<RvTopProductAdapter.viewHolder>() {
    init {
        list.sortedByDescending { it.sold }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:List<TopProductResponseModel>){
        this.list = list
        this.list.sortedByDescending { it.sold }
        notifyDataSetChanged()
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgProduct: ShapeableImageView
        val imgTop : ImageView
        val tvName: TextView
        val tvSold:TextView
        val tvDescription:TextView

        init {
            imgProduct = itemView.findViewById(R.id.img_product)
            tvName = itemView.findViewById(R.id.tv_name)
            imgTop = itemView.findViewById(R.id.img_top)
            tvSold = itemView.findViewById(R.id.tv_sold)
            tvDescription = itemView.findViewById(R.id.tv_description)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_top_product,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
      holder.itemView.apply {
          Glide.with(context).load(list[position].imgPreview).into(holder.imgProduct)
          holder.tvName.text = list[position].name
          holder.tvSold.text = "Đã bán: ${list[position].sold}"
          holder.tvDescription.text = list[position].description
          if(position == 0){
              holder.imgTop.setImageResource(R.drawable.icon_top1)
          }else if(position == 1){
              holder.imgTop.setImageResource(R.drawable.top_2)
          }else{
              holder.imgTop.setImageResource(R.drawable.top_3)
          }
      }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}