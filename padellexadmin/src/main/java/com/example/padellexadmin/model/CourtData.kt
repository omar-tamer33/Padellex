package com.example.padellexadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourtData(
    var id : String = "",
    var courtName : String = "",
    var courtPrice : Double = 0.0,
    var courtLocation : String = "",
    var courtAvailability : Boolean = false,
    var latitude : Double? = 0.0,
    var longitude : Double? = 0.0,
    var imageUrl : String? = ""): Parcelable
