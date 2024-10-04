package vn.clothing.store.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.recyclerview.widget.RecyclerView
import vn.clothing.store.R
import vn.clothing.store.models.VoucherModel

class RvSelectedVoucherAdapter(  private var idVoucherOld: String?,private val onClick:Consumer<VoucherModel?>): RecyclerView.Adapter<RvSelectedVoucherAdapter.viewHolder>() {

    private var list: List<VoucherModel> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(lst: List<VoucherModel>) {
        list = lst.toMutableList()
        notifyDataSetChanged()
    }


    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var idVoucher: TextView
        var tvContent: TextView
        var tvTitle: TextView
        var btnSelect: AppCompatButton

        init {
            idVoucher = itemView.findViewById(R.id.idVoucher)
            tvContent = itemView.findViewById(R.id.tvContent)
            tvTitle = itemView.findViewById(R.id.tvTitle)
            btnSelect = itemView.findViewById(R.id.btn_select)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.viewholder_voucher, parent, false)
        return viewHolder(view)
    }

    var pos: Int = -1

    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            holder.idVoucher.text = list[position].id
            holder.tvContent.text = list[position].description
            holder.tvTitle.text = list[position].title
            holder.btnSelect.text = "Sử dụng"

            if (idVoucherOld == list[position].id) {
                idVoucherOld = null
                pos = position
            }

            holder.btnSelect.setOnClickListener {
                val previousSelected = pos
                if (pos != position) {
                    pos = position
                    onClick.accept(list[position])
                } else {
                    pos = -1
                    onClick.accept(null)
                }

                notifyItemChanged(previousSelected) // Cập nhật ViewHolder trước đó nếu có
                notifyItemChanged(position) // Cập nhật ViewHolder đang được chọn
            }

            if (pos == position) {
                holder.btnSelect.text = "Bỏ chọn"
                holder.btnSelect.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.btnSelect.backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
            } else {
                holder.btnSelect.text = "Sử dụng"
                holder.btnSelect.backgroundTintList = ContextCompat.getColorStateList(context, R.color.gray_light)
                holder.btnSelect.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            }

        }
    }


    override fun getItemCount(): Int {
        return list.size
    }
}