package com.example.padellexadmin.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.padellexadmin.R
import com.example.padellexadmin.model.CourtData

class CourtAdapter(val list: MutableList<CourtData>) : Adapter<CourtAdapter.courtItemViewHolder>() {

    var courtItemClickListener:onCourtItemClickListener?=null
    var deleteItemClickListener:onDeleteItemClickListener?=null

    fun updateAdapter(newList: List<CourtData>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): courtItemViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view : View = inflater.inflate(R.layout.item_court,parent,false)
        return courtItemViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: courtItemViewHolder, position: Int) {
        val courtData : CourtData = list.get(position)
        holder.bind(courtData)
        holder.itemView.setOnClickListener {
            courtItemClickListener?.onCourtItemClick(courtData,position)
        }
        holder.deleteButton.setOnClickListener {
            deleteItemClickListener?.onDeleteClick(courtData, position)
        }
    }

    inner class courtItemViewHolder(itemView: View) : ViewHolder(itemView) {
        private val courtName : TextView = itemView.findViewById(R.id.courtNameTv)
        private val courtPrice : TextView = itemView.findViewById(R.id.courtPriceTv)
        private val courtLocation : TextView = itemView.findViewById(R.id.courtLocationTv)
         val deleteButton : ImageView = itemView.findViewById(R.id.deleteBtn)
        fun bind(courtData: CourtData) {
            courtName.text = courtData.courtName
            courtPrice.text = courtData.courtPrice.toString()
            courtLocation.text = courtData.courtLocation
        }
    }
    interface onCourtItemClickListener{
        fun onCourtItemClick(courtData: CourtData, position: Int)
    }

    interface onDeleteItemClickListener{
        fun onDeleteClick(courtData: CourtData,position: Int)
    }
}