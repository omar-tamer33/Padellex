package com.example.padellex.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserChatsData(
    val chatId : String = "",
    val participants : List<String> = emptyList(),
    val lastMessage : String = "",
    val timestamp : Long? = null,
    val firstName : String = "",
    val lastName : String = "",
    val imageUrl : String? = ""
) : Parcelable
