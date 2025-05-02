package com.example.padellex.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.padellex.R
import com.example.padellex.databinding.ItemChatsBinding
import com.example.padellex.model.UserChatsData
import com.example.padellex.model.UserInfo

class ChatsAdapter(val usersList: List<UserChatsData?>) : Adapter<ChatsItemViewHolder>() {
    var onItemClick : ((item : UserChatsData) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChatsBinding.inflate(inflater,parent,false)
        return ChatsItemViewHolder(binding)
    }

    override fun getItemCount(): Int = usersList.size

    override fun onBindViewHolder(holder: ChatsItemViewHolder, position: Int) {
        val userChatsData = usersList.get(position)
        if (userChatsData != null) {
            holder.bind(userChatsData)
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(userChatsData)
            }
        }
    }
}

class ChatsItemViewHolder(val binding : ItemChatsBinding) : ViewHolder(binding.root){
    fun bind(userChatsData: UserChatsData){
       binding.userNameTv.text = "${userChatsData.firstName} ${userChatsData.lastName}"
        binding.lastMessageTv.text = userChatsData.lastMessage
       Glide.with(itemView.context)
           .load(userChatsData.imageUrl)
           .placeholder(R.drawable.blank_profile_picture)
           .into(binding.profileImage)
    }
}