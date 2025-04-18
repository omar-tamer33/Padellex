package com.example.padellexadmin.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.padellexadmin.Repositories.TimeSlotsRepository
import com.example.padellexadmin.Repositories.UserBookingRepository
import com.example.padellexadmin.model.UserBookingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(private val userBookingRepository: UserBookingRepository , private val timeSlotsRepository: TimeSlotsRepository) : ViewModel() {
    val list = MutableLiveData<List<UserBookingItem>>()
    val onDeleteEvent = MutableLiveData<UserBookingItem>()

     fun getUserBookingData() {
        userBookingRepository.getAllBookings() { userBookingList ->
           list.value = userBookingList
        }
    }

    fun onDeleteClick(userBookingItem: UserBookingItem){
        onDeleteEvent.value = userBookingItem
    }

    fun preformDelete(userBookingItem: UserBookingItem){
        userBookingRepository.deleteUserBooking(userBookingItem.userId,userBookingItem.bookingId)
        timeSlotsRepository.unBookTimeSlot(userBookingItem)
    }
}