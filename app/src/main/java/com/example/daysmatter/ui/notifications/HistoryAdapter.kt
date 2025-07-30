package com.example.daysmatter.ui.notifications

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daysmatter.R
import com.example.daysmatter.ui.notifications.Entity.HistoryEvent

class HistoryAdapter(private val list: List<HistoryEvent>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val dateText:TextView=view.findViewById(R.id.date_text)
        val titleText:TextView=view.findViewById(R.id.title_text)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = list[position]
        Log.d("HistoryAdapter", "绑定数据: position=$position, date=${event.date}, title=${event.title}")
        holder.dateText.text = event.date
        holder.titleText.text = event.title
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
