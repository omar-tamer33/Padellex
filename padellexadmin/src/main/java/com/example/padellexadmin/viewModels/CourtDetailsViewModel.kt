package com.example.padellexadmin.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.padellexadmin.Repositories.CourtsRepository
import com.example.padellexadmin.Repositories.TimeSlotsRepository
import com.example.padellexadmin.model.CourtData
import com.example.padellexadmin.model.TimeSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class CourtDetailsViewModel @Inject constructor(private val timeSlotsRepository: TimeSlotsRepository, private val courtsRepository: CourtsRepository, private val cloudinary: Cloudinary): ViewModel() {
    val list = MutableLiveData<List<TimeSlot>>()
    val courtDataSuccess = MutableLiveData<CourtData?>()
    val isImageAdded = MutableLiveData<Boolean>()
    val url = MutableLiveData<String>()

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

    fun uploadImage(inputStream: InputStream?, courtId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap())
                val imageUrl = result.get("secure_url") as String
                val publicId = result.get("public_id") as String
                deleteOldImage(courtId)
                courtsRepository.updateCourtImage(courtId,imageUrl,publicId)
                isImageAdded.postValue(true)
                url.postValue(imageUrl)
            } catch (e: Exception) {
                Log.e("TAG", "uploadImage: error $e", )
                isImageAdded.postValue(false)
                url.postValue("")
            }
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