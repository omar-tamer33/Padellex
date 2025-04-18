package com.example.padellex.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padellex.Repositories.TimeSlotsRepository
import com.example.padellex.Repositories.UserBookingRepository
import com.example.padellex.model.UserBookingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserBookingViewModel @Inject constructor(private val userBookingRepository: UserBookingRepository , private val timeSlotsRepository: TimeSlotsRepository) : ViewModel() {
    val bookingList = MutableLiveData<List<UserBookingItem>>()
    val deleteBookingEvent = MutableLiveData<UserBookingItem>()

     fun getUserBookingData(userId : String) {
        viewModelScope.launch {
           userBookingRepository.getAllUserBooking(userId){userBookingList->
               bookingList.postValue(userBookingList)
           }
        }
    }

    private fun deleteUserBooking(userId: String, bookingId : String){
        viewModelScope.launch {
            userBookingRepository.deleteUserBooking(userId,bookingId)
        }
    }

    private fun unBookTimeSlot(userBookingItem: UserBookingItem){
        viewModelScope.launch {
            timeSlotsRepository.unBookTimeSlot(userBookingItem)
        }
    }


    fun onDeleteClick(userBookingItem: UserBookingItem){
        deleteBookingEvent.value = userBookingItem
    }

    fun preformDelete(userBookingItem: UserBookingItem,userId: String){
        deleteUserBooking(userId,userBookingItem.bookingId)
        unBookTimeSlot(userBookingItem)
    }
}