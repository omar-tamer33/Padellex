package com.example.padellexadmin.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.padellexadmin.Repositories.CourtsRepository
import com.example.padellexadmin.model.CourtData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourtsViewModel @Inject constructor(private val courtsRepository: CourtsRepository): ViewModel() {
    val list = MutableLiveData<List<CourtData>>()
    val onDeleteEvent = MutableLiveData<CourtData>()

    fun onDeleteClick(courtData: CourtData){
        onDeleteEvent.value = courtData
    }

    fun preformDelete(courtId : String){
        courtsRepository.deleteCourt(courtId)
    }

    fun getCourtsData(){
        courtsRepository.getAllCourts {courtItems ->
            list.value = courtItems
        }
    }
}