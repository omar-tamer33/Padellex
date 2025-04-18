package com.example.padellexadmin.Adapters

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.padellexadmin.R
import com.example.padellexadmin.databinding.ItemTimeSlotBinding
import com.example.padellexadmin.model.TimeSlot

class TimeAdapter(val timeList: MutableList<TimeSlot>) : Adapter<TimeViewHolder>() {
    var onTimeItemClickListener : OnTimeClickListener? = null

    fun updateAdapter(newList: List<TimeSlot>){
        timeList.clear()
        timeList.addAll(newList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTimeSlotBinding.inflate(inflater,parent,false)
        return TimeViewHolder(binding)
    }

    override fun getItemCount(): Int = timeList.size

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val item = timeList.get(position)
        holder.bind(item)
        holder.bindIsClosed(item.booked)
        holder.itemView.setOnClickListener {
            onTimeItemClickListener?.onTimeClick(item,position)
        }
    }

}

class TimeViewHolder(val binding: ItemTimeSlotBinding) : ViewHolder(binding.root){
    fun bind(item: TimeSlot) {
        binding.timeSlot.text = item.timeKey
    }
    fun bindIsClosed(isClosed : Boolean){
        if (isClosed){
            binding.timeSlot.setTextColor(Color.GRAY)
            binding.timeSlot.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.timeSlot.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(binding.timeSlot.context, R.drawable.ic_confirm),
                null
            )        }else{
            binding.timeSlot.setTextColor(Color.BLACK)
            binding.timeSlot.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(binding.timeSlot.context, R.drawable.ic_x),
                null
            )        }
    }
}

interface OnTimeClickListener{
    fun onTimeClick(timeSlot: TimeSlot , position: Int)
}