package com.example.padellex.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class UserInfo(val id : String? = null ,
                    val firstName : String = "",
                    val lastName : String = "",
                    val phone : String = "",
                    val imageUrl : String = "" ,
                    val publicId : String = "",
                    val strikesCount : Int = 0,
                    val power : String = "",
                    val speed : String = "",
                    val playStyle : String = "")
