package com.example.padellex.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.padellex.databinding.UserBookingItemBinding
import com.example.padellex.model.UserBookingItem

class UserBookingAdapter(val userBookingList: MutableList<UserBookingItem>) : Adapter<UserBookingItemViewHolder>() {
    var deleteBtnClickListener : OnDeleteClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBookingItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserBookingItemBinding.inflate(inflater,parent,false)
        return UserBookingItemViewHolder(binding)
    }

    override fun getItemCount(): Int = userBookingList.size

    override fun onBindViewHolder(holder: UserBookingItemViewHolder, position: Int) {
        val userBookingItem = userBookingList[position]
        holder.bind(userBookingItem)
        holder.binding.deleteBtn.setOnClickListener {
            deleteBtnClickListener?.onDeleteClick(userBookingItem,position)
        }

    }

}

class UserBookingItemViewHolder(val binding: UserBookingItemBinding) : ViewHolder(binding.root){
    fun bind(userBookingItem: UserBookingItem) {
        binding.bookingDateTv.text = userBookingItem.bookingDate
        binding.bookingTimeTv.text = userBookingItem.bookingTime.toString()
        binding.courtNameTv.text = userBookingItem.courtName
        binding.courtLocationTv.text = userBookingItem.courtLocation
        binding.courtPriceTv.text = "${userBookingItem.courtPrice} EGP"
    }

}

interface OnDeleteClickListener{
    fun onDeleteClick(userBookingItem: UserBookingItem, position: Int)
}