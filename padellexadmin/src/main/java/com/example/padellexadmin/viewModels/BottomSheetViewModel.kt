package com.example.padellexadmin.viewModels

import androidx.lifecycle.ViewModel
import com.example.padellexadmin.Repositories.CourtsRepository
import com.example.padellexadmin.model.CourtData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BottomSheetViewModel @Inject constructor(private val courtsRepository: CourtsRepository) : ViewModel() {

    fun addCourtInDatabase(courtName : String , courtLocation : String, courtPrice : Double, courtLatitude : Double , courtLongitude : Double, id : String){
        val courtData = CourtData(courtName = courtName, courtLocation = courtLocation, courtPrice = courtPrice, courtAvailability = false , id = id , latitude = courtLatitude , longitude = courtLongitude)
        courtsRepository.addCourt(courtData)
    }
}