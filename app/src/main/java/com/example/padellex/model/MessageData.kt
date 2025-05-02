package com.example.padellex.model

data class MessageData(
    val senderId : String = "",
    val text : String = "",
    val timestamp : Long? = null)
