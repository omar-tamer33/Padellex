package com.example.padellex.model

data class UserBookingItem(
                           val courtName:String = "",
                           val courtPrice:String = "",
                           val courtLocation:String = "",
                           val bookingDate:String = "",
                           val bookingTime:List<String>? = null,
                           val bookingId:String = "",
                           val courtId:String = "",
                            val userId:String = "")
