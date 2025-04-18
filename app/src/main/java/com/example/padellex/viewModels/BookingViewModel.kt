package com.example.padellex.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padellex.Repositories.TimeSlotsRepository
import com.example.padellex.Repositories.UserBookingRepository
import com.example.padellex.model.CourtItem
import com.example.padellex.model.TimeSlot
import com.example.padellex.model.UserBookingItem
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(private val timeSlotsRepository: TimeSlotsRepository , private val auth: FirebaseAuth , private val userBookingRepository: UserBookingRepository): ViewModel() {
    val list = MutableLiveData<List<TimeSlot>>()



    fun generateTimeSlotsForDate(dateStr : String , courtId : String) {
        viewModelScope.launch {
            timeSlotsRepository.getTimeSlots(dateStr = dateStr, courtId = courtId) { timeSlots ->
                list.postValue(timeSlots)
            }
        }
    }

    fun calculatePrice(numOfHours : Int , price : Double) : Double{
        return price * numOfHours
    }

     fun bookSlots(listOfSlots : List<TimeSlot>, courtItem: CourtItem, dateStr: String) {
        val courtId = courtItem.id
        val userId = auth.currentUser!!.uid
        val bookingId = UUID.randomUUID().toString()
        val bookingTimeList = mutableListOf<String>()
        for (timeSlot in listOfSlots){
            timeSlotsRepository.bookTimeSlot(courtId,dateStr,timeSlot,userId)
            bookingTimeList.add(timeSlot.timeKey)
        }
        userBookingRepository.addUserBooking(userId, UserBookingItem(
            courtName = courtItem.courtName,
            courtPrice = calculatePrice(bookingTimeList.size,courtItem.courtPrice).toString(),
            courtLocation = courtItem.courtLocation,
            bookingDate = dateStr,
            bookingTime = bookingTimeList,
            bookingId = bookingId,
            courtId = courtId,
            userId = userId)
        )
    }

    fun generateTodaySlotsIfNeeded(courtId: String,dateStr: String){
        timeSlotsRepository.generateTodaySlotsIfNeeded(courtId,dateStr)
    }
}

