package com.example.padellexadmin.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.padellexadmin.Repositories.TimeSlotsRepository
import com.example.padellexadmin.model.TimeSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourtDetailsViewModel @Inject constructor(private val timeSlotsRepository: TimeSlotsRepository): ViewModel() {
    val list = MutableLiveData<List<TimeSlot>>()

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
}