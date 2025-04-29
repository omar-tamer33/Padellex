package com.example.padellex.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.example.padellex.Repositories.TimeSlotsRepository
import com.example.padellex.Repositories.UserBookingRepository
import com.example.padellex.Repositories.UserRepository
import com.example.padellex.model.UserBookingItem
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserBookingViewModel @Inject constructor(private val userBookingRepository: UserBookingRepository , private val timeSlotsRepository: TimeSlotsRepository , private val userRepository: UserRepository , private val auth: FirebaseAuth , private val cloudinary: Cloudinary) : ViewModel() {
    val bookingList = MutableLiveData<List<UserBookingItem>>()
    val deleteBookingEvent = MutableLiveData<UserBookingItem>()
    val userDelete = MutableLiveData<Boolean>()

     fun getUserBookingByDate(userId : String,dateStr : String) {
        viewModelScope.launch {
           userBookingRepository.getAllUserBookingByDate(userId,dateStr){userBookingList->
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

   private fun checkUserStrikes(userId: String){
        viewModelScope.launch {
            try {
                val currentStrikesCount = userRepository.checkUserStrikes(userId)
                if (currentStrikesCount == 3){
                    userDelete.postValue(true)
                    deleteOldImage(userId)
                    userBookingRepository.deleteAllUserBooking(userId)
                    auth.currentUser?.delete()?.await()
                    userRepository.deleteUser(userId)
                }else{
                    userRepository.incUserStrikes(userId)
                    userDelete.postValue(false)
                }
            }catch (e : Exception){
                Log.e("TAG", "checkUserStrikes: error $e", )
            }
        }
    }

    fun preformDelete(userBookingItem: UserBookingItem,userId: String){
        deleteUserBooking(userId,userBookingItem.bookingId)
        unBookTimeSlot(userBookingItem)
        checkUserStrikes(userId)
    }

    private fun deleteOldImage(userId : String){
        userRepository.getUserPublicId(userId){ publicId ->
            if (publicId != null){
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val options = mapOf("resource_type" to "image")
                        cloudinary.uploader().destroy(publicId, options)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun getCurrentStrikesCount(userId: String): Int? = runBlocking(Dispatchers.IO) {
        try {
            userRepository.checkUserStrikes(userId)
        } catch (e: Exception) {
            Log.e("TAG", "Error fetching strikes", e)
            null
        }
    }
}