package com.example.padellexadmin

data class CourtData(
    var id : String,
    var courtName : String? = null,
    var courtPrice : Double? = null,
    var courtLocation : String? = null,
    var courtAvailability : Boolean? = false,
    var latitude : Double? = null,
    var longitude : Double? = null
)
