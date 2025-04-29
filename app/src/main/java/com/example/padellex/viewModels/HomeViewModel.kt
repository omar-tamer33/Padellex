package com.example.padellex.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.padellex.Repositories.UserRepository
import com.example.padellex.api.PlayerRatingService
import com.example.padellex.model.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val playerRatingService: PlayerRatingService , private val userRepository: UserRepository , private val cloudinary: Cloudinary) : ViewModel(){
    val userDetails = MutableLiveData<UserInfo?>()
    val playerRate = MutableLiveData<Map<String,String>?>()


    private fun getPlayerRating(userId: String, videoPublicId : String){
        viewModelScope.launch {
            try {
              val rate = playerRatingService.getPlayerRating(videoPublicId)
                val shootSpeed = rate.analysis?.player1?.avgShotSpeedKmh
                val playerSpeed = rate.analysis?.player1?.avgMovementSpeedKmh
                if (shootSpeed != null && playerSpeed != null) {
                    val playStyle = calculatePlayStyle(playerSpeed, shootSpeed)
                    updateUserRate(
                        userId = userId,
                        shootSpeed = shootSpeed.toString(),
                        playerSpeed = playerSpeed.toString(),
                        playStyle =playStyle
                    )
                    playerRate.postValue(mapOf("shootSpeed" to shootSpeed.toString(),"playerSpeed" to playerSpeed.toString(),"playStyle" to playStyle))
                }
            } catch (e: Exception) {
                playerRate.postValue(null)
                Log.e("TAG", "getPlayerRating: error $e",)
            }
        }
    }

    fun getUserDetails(userId : String){
        viewModelScope.launch {
            try {
               val userInfo = userRepository.getUser(userId)
                userDetails.postValue(userInfo)
            } catch (e: Exception) {
                userDetails.postValue(null)
                Log.e("TAG", "getUserDetails: error $e",)
            }
        }
    }

    fun uploadVideo(inputStream: InputStream?, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = cloudinary.uploader().upload(inputStream, mapOf(
                    "resource_type" to "video",
                    "chunk_size" to 50_000_000,
                    "max_file_size" to 500_000_000
                ))
                val publicId = result.get("public_id") as String
                getPlayerRating(userId,publicId)
            } catch (e: Exception) {
                Log.e("TAG", "uploadVideo: error $e", )
            }
        }
    }

    private fun updateUserRate(userId: String, shootSpeed : String, playerSpeed : String, playStyle : String){
        viewModelScope.launch {
            try {
                userRepository.updateUserRate(userId, shootSpeed, playerSpeed, playStyle)
            } catch (e: Exception) {
                Log.e("TAG", "updateUserRate: error $e",)
            }
        }
    }

    private fun calculatePlayStyle(playerSpeed: Double, shootSpeed: Double) : String{
        val ratio = playerSpeed / shootSpeed
        if (ratio > 1){
            return "Aggressive"
        }else if (ratio < 1){
            return "Defensive"
        }else{
            return "Balance"
        }
    }
}