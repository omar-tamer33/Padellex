package com.example.padellex.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.padellex.R
import com.example.padellex.model.MessageData

class MessagesAdapter(val list: List<MessageData> ,val userId : String) : Adapter<MessagesAdapter.MessagesItemViewHolder>() {

    val SENT = 1
    val RECIVED = 2
    override fun getItemViewType(position: Int): Int {
        if (list.get(position).senderId == userId){
            return SENT
        }else{
            return RECIVED
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesItemViewHolder {
        if (viewType == SENT){
            val inflater = LayoutInflater.from(parent.context)
            val binding = inflater.inflate(R.layout.item_message_sent,parent,false)
            return MessagesItemViewHolder(binding)
        }else{
            val inflater = LayoutInflater.from(parent.context)
            val binding = inflater.inflate(R.layout.item_message_recived,parent,false)
            return MessagesItemViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MessagesItemViewHolder, position: Int) {
        val itemView = list.get(position)
        holder.bind(itemView)
    }

    class MessagesItemViewHolder(val view : View) : ViewHolder(view){
        fun bind(itemView: MessageData) {
            val messageTv = view.findViewById<TextView>(R.id.messageTv)
            messageTv.text = itemView.text
        }
    }

}