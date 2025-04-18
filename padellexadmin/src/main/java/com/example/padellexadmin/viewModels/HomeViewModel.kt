package com.example.padellexadmin.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.padellexadmin.Repositories.UserBookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val userBookingRepository: UserBookingRepository): ViewModel() {

    fun bookingNotification(context : Context){
        userBookingRepository.bookingNotification(context)
    }
}