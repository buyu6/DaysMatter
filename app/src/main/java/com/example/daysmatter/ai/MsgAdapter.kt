package com.example.daysmatter.ai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daysmatter.R

class MsgAdapter(private var msgList: List<Msg>) : RecyclerView.Adapter<MsgAdapter.MsgViewHolder>() {
    open class MsgViewHolder(view: View): RecyclerView.ViewHolder(view)
    class LeftViewHolder(view: View): MsgViewHolder(view) {
        val leftMsg: TextView = view.findViewById(R.id.leftMsg)
    }

    class RightViewHolder(view: View): MsgViewHolder(view) {
        val rightMsg: TextView = view.findViewById(R.id.rightMsg)
    }
    override fun getItemViewType(position:Int): Int {
        val msg=msgList[position]
        return msg.type
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgViewHolder {
        if (viewType==Msg.TYPE_RESERVED){
            val view=LayoutInflater.from(parent.context).inflate(R.layout.msg_left,parent,false)
            return LeftViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_right,parent,false)
            return RightViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val msg=msgList[position]
        when(holder){
            is LeftViewHolder->holder.leftMsg.text=msg.content
            is RightViewHolder->holder.rightMsg.text=msg.content
        }
    }

    override fun getItemCount(): Int {
        return msgList.size
    }
    fun updateMessages(newMessages: List<Msg>) {
        val oldSize = msgList.size
        msgList = newMessages
        if (oldSize < newMessages.size) {
            // 新增消息，使用notifyItemInserted
            notifyItemInserted(oldSize)
        } else {
            // 其他情况，刷新整个列表
            notifyDataSetChanged()
        }
    }
}