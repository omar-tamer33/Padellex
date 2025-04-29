package com.example.padellexadmin.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudinary.Cloudinary
import com.example.padellexadmin.Repositories.CourtsRepository
import com.example.padellexadmin.model.CourtData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourtsViewModel @Inject constructor(private val courtsRepository: CourtsRepository , private val cloudinary: Cloudinary): ViewModel() {
    val list = MutableLiveData<List<CourtData>>()
    val onDeleteEvent = MutableLiveData<CourtData>()

    fun onDeleteClick(courtData: CourtData){
        onDeleteEvent.value = courtData
    }

    fun preformDelete(courtId : String){
        deleteOldImage(courtId)
        courtsRepository.deleteCourt(courtId)
    }

    fun getCourtsData(){
        courtsRepository.getAllCourts {courtItems ->
            list.value = courtItems
        }
    }

    private fun deleteOldImage(courtId : String){
        courtsRepository.getCourtPublicId(courtId){ publicId ->
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
}