package com.example.daysmatter.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.daysmatter.R
import com.example.daysmatter.ui.home.Room.IconItem

class IconAdapter(
    private val icons: List<IconItem>,
    private val onIconSelected: (Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    inner class IconViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconImage: ImageView = view.findViewById(R.id.iconImage)
        val selectedOverlay: ImageView = view.findViewById(R.id.selectedOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon_item, parent, false)
       val holder=IconViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int = icons.size

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val icon = icons[position]
        holder.iconImage.setImageResource(icon.resId)
        holder.selectedOverlay.visibility = if (icon.isSelected) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            icons.forEach { it.isSelected = false }
            icon.isSelected = true
            notifyDataSetChanged()
            onIconSelected(icon.resId)
        }
    }
}
