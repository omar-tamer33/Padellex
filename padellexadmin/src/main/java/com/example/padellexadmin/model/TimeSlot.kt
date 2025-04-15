package com.example.padellexadmin.model

data class TimeSlot (var timeKey: String = "",
                     var booked: Boolean = false,
                     var userId: String? = null,
                     var timestamp: Long = 0L)