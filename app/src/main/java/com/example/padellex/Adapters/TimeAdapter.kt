package com.example.padellex.Adapters

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.padellex.R
import com.example.padellex.databinding.ItemTimeSlotBinding
import com.example.padellex.model.TimeSlot

class TimeAdapter(val timeList: MutableList<TimeSlot>) : Adapter<TimeViewHolder>() {
    var onTimeItemSelectedListener : OnTimeSelectedListener? = null
    private val selectedItems = mutableSetOf<Int>()
    private val selectedSlots = mutableListOf<TimeSlot>()

    fun updateAdapter(newList: List<TimeSlot>){
        timeList.clear()
        timeList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getSelectedSlots(): List<TimeSlot> = selectedSlots.toList()

    fun clearSelections() {
        selectedItems.clear()
        selectedSlots.clear()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTimeSlotBinding.inflate(inflater,parent,false)
        return TimeViewHolder(binding)
    }

    override fun getItemCount(): Int = timeList.size

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val item = timeList[position]
        holder.bind(item)
        holder.bindIsClosed(item.booked)
        if (!item.booked) {
            if (selectedItems.contains(position)) {
                holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.Cyan)
                )
            } else {
                holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.white)
                )
            }
        }

        holder.itemView.setOnClickListener {
            if (item.booked) return@setOnClickListener
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
                selectedSlots.remove(item)
            } else {
                selectedItems.add(position)
                selectedSlots.add(item)
            }
            notifyItemChanged(position)
            onTimeItemSelectedListener?.onTimeSelected(item,position,selectedSlots)
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
        }else{
            binding.timeSlot.setTextColor(Color.BLACK)
            binding.timeSlot.paintFlags = 0
        }
    }
}


interface OnTimeSelectedListener{
    fun onTimeSelected(item: TimeSlot , position: Int , selectedSlots : MutableList<TimeSlot>)
}

