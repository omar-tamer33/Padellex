package com.example.padellexadmin.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.padellexadmin.Repositories.CourtsRepository
import com.example.padellexadmin.Repositories.UserBookingRepository
import com.example.padellexadmin.model.CourtData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val userBookingRepository: UserBookingRepository , private val courtsRepository: CourtsRepository): ViewModel() {
    val isAdded = MutableLiveData<Boolean>()

    fun bookingNotification(context : Context){
        userBookingRepository.bookingNotification(context)
    }

    fun addCourtInDatabase(courtName : String , courtLocation : String, courtPrice : Double, courtLatitude : Double , courtLongitude : Double, id : String){
        val courtData = CourtData(courtName = courtName, courtLocation = courtLocation, courtPrice = courtPrice, courtAvailability = false , id = id , latitude = courtLatitude , longitude = courtLongitude)
        courtsRepository.addCourt(courtData){success->
            if (success){
                isAdded.value = true
            }else{
                isAdded.value = false
            }
        }
    }
}