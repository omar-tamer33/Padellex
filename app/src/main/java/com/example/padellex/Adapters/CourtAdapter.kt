package com.example.padellex.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.padellex.CourtItem
import com.example.padellex.R

class CourtAdapter(val mutableList: MutableList<CourtItem>) : Adapter<CourtAdapter.courtItemViewHolder>() {

    var courtItemClickListener:onCourtItemClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): courtItemViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view : View = inflater.inflate(R.layout.item_court,parent,false)
        return courtItemViewHolder(view)
    }

    override fun getItemCount(): Int = mutableList.size

    override fun onBindViewHolder(holder: courtItemViewHolder, position: Int) {
        val courtItem :CourtItem= mutableList.get(position)
        holder.bind(courtItem)
        holder.itemView.setOnClickListener {
            courtItemClickListener?.onCourtItemClick(courtItem,position)
        }
    }

    inner class courtItemViewHolder(itemView: View) : ViewHolder(itemView) {
        private val courtName : TextView = itemView.findViewById(R.id.courtNameTv)
        private val courtPrice : TextView = itemView.findViewById(R.id.courtPriceTv)
        private val courtLocation : TextView = itemView.findViewById(R.id.courtLocationTv)
        fun bind(courtItem: CourtItem) {
            courtName.text = courtItem.courtName
            courtPrice.text = courtItem.courtPrice.toString()
            courtLocation.text = courtItem.courtLocation
        }

    }
    interface onCourtItemClickListener{
        fun onCourtItemClick(courtItem: CourtItem,position: Int)
    }
}