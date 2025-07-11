package com.example.daysmatter.ui.home

import android.app.Activity
import android.app.AlertDialog

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.lifecycle.LiveData


import androidx.recyclerview.widget.RecyclerView
import com.example.daysmatter.EditMsgActivity
import com.example.daysmatter.MyApplication
import com.example.daysmatter.R
import com.example.daysmatter.ShowMsgActivity
import com.example.daysmatter.ui.home.Room.Message
import com.example.daysmatter.ui.home.Room.MessageDao
import com.example.daysmatter.ui.home.Room.MessageDatabase
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.concurrent.thread
import kotlin.math.abs

class MsgAdapter(private val activity: Activity) : RecyclerView.Adapter<MsgAdapter.ViewHolder>() {
    private lateinit var today: LocalDate
    private var data = mutableListOf<Message>()
    private val dao: MessageDao = MessageDatabase.getDatabase(activity).messageDao()

    fun submitList(newList: List<Message>) {
        data = newList.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.firsttitle)
        val time: TextView = itemView.findViewById(R.id.firsttime)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val msg = data[position]
                    val intent = Intent(activity, ShowMsgActivity::class.java).apply {
                        putExtra("title", msg.title)
                        putExtra("time", msg.time)
                        putExtra("aimdate", msg.aimdate.toString())
                    }
                    activity.startActivity(intent)
                }
            }

            itemView.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val msg = data[position]
                    AlertDialog.Builder(activity).apply {
                        setCancelable(true)
                        setPositiveButton("删除") { _, _ ->
                            thread {
                                dao.deletemessage(msg)
                                activity.runOnUiThread {
                                    data.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position, data.size - position)
                                }
                            }
                        }
                        setNegativeButton("编辑") { _, _ ->
                            val intent = Intent(activity, EditMsgActivity::class.java).apply {
                                putExtra("title", msg.title)
                                putExtra("time", msg.time)
                                putExtra("aimdate", msg.aimdate.toString())
                            }
                            activity.startActivityForResult(intent, 1) // 如果你暂时还不想换新版 API
                        }
                        show()
                    }
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = data[position]
        today = LocalDate.now()
        val aimdate = LocalDate.parse(message.aimdate)
        val daysBetween = ChronoUnit.DAYS.between(today, aimdate).toInt()

        holder.time.text = kotlin.math.abs(daysBetween).toString()
        holder.title.text = when {
            daysBetween == 0 -> "${message.title}就是今天"
            daysBetween > 0 -> "${message.title}还有"
            else -> "${message.title}已经"
        }
    }

    override fun getItemCount(): Int = data.size
}
