package vn.clothing.store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.clothing.store.R
import vn.clothing.store.models.ItemDashboardViewUser

class RvDashboardItemViewUser(private val lst: ArrayList<ItemDashboardViewUser>) :
    RecyclerView.Adapter<RvDashboardItemViewUser.ItemDashboardViewHolder>() {

    inner class ItemDashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView
        var title: TextView
        var description: TextView

        init {
            icon = itemView.findViewById(R.id.icon)
            title = itemView.findViewById(R.id.title)
            description = itemView.findViewById(R.id.description)
        }

        fun bind(item: ItemDashboardViewUser) {
            icon.setImageResource(item.icon)
            title.text = item.title
            description.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_item_block_user_fragment, parent, false
        )
        return ItemDashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemDashboardViewHolder, position: Int) {
        holder.bind(lst[position])
    }

    override fun getItemCount(): Int {
        return lst.size
    }
}