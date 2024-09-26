package vn.clothing.store.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.util.Consumer
import androidx.recyclerview.widget.RecyclerView
import vn.clothing.store.R
import vn.clothing.store.models.DeliveryInformation

class RvMyAddressAdapter(private var list:List<DeliveryInformation>, private val onClick:Consumer<DeliveryInformation>) : RecyclerView.Adapter<RvMyAddressAdapter.viewHolder>() {
    @SuppressLint("NotifyDataSetChanged")
    fun setData(lst:List<DeliveryInformation>){
        this.list = lst
        notifyDataSetChanged()
    }

    class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tvContact: TextView
        var tvAddress: TextView
        var tvIsDefault: TextView
        var rdoButton: RadioButton

        init {
            tvContact = itemView.findViewById(R.id.tv_contact)
            tvAddress = itemView.findViewById(R.id.tv_address)
            tvIsDefault = itemView.findViewById(R.id.tv_is_default)
            rdoButton = itemView.findViewById(R.id.rdoButton)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_delivery_information,parent,false)
        return viewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.itemView.apply {
            holder.rdoButton.visibility = View.GONE
            holder.tvAddress.text = list[position].details
            holder.tvContact.text = "${list[position].numberPhone} | ${list[position].fullName}"
            if(list[position].isDefault){
                holder.tvIsDefault.visibility = View.VISIBLE
            }else{
                holder.tvIsDefault.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}