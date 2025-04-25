package com.example.padellexadmin.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padellexadmin.Repositories.CourtsRepository
import com.example.padellexadmin.Repositories.TimeSlotsRepository
import com.example.padellexadmin.model.CourtData
import com.example.padellexadmin.model.TimeSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourtDetailsViewModel @Inject constructor(private val timeSlotsRepository: TimeSlotsRepository, private val courtsRepository: CourtsRepository): ViewModel() {
    val list = MutableLiveData<List<TimeSlot>>()
    val courtDataSuccess = MutableLiveData<CourtData?>()

    fun timeSlotAvailability(courtId: String,dateStr: String,timeSlot: TimeSlot){
        timeSlotsRepository.timeSlotAvailability(courtId = courtId, dateStr = dateStr, timeSlot = timeSlot)
    }

    fun generateTimeSlotsForDate(dateStr : String, courtId : String) {
        timeSlotsRepository.getTimeSlots(dateStr = dateStr , courtId = courtId) { timeSlots ->
            list.value = timeSlots
        }
    }

    fun generateTodaySlotsIfNeeded(courtId: String,dateStr: String){
        timeSlotsRepository.generateTodaySlotsIfNeeded(courtId,dateStr)
    }

    fun updateCourt(courtData: CourtData){
        courtsRepository.updateCourt(courtData){

        }
    }

    fun getCourtDetails(courtId: String){
        courtsRepository.getCourtDetails(courtId) {courtData->
            if (courtData != null) {
                courtDataSuccess.postValue(courtData)
            }else {
                courtDataSuccess.postValue(null)
            }
        }
    }
}