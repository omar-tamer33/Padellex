package com.example.padellex.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.padellex.Repositories.UserRepository
import com.example.padellex.model.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val userRepository: UserRepository , private val cloudinary: Cloudinary) : ViewModel() {
    val success = MutableLiveData<Boolean>()
    val isImageAdded = MutableLiveData<Boolean>()
    val url = MutableLiveData<String>()
    val userInfoItem = MutableLiveData<UserInfo?>()



     fun updateUserInformation(userId : String,phone : String){
         viewModelScope.launch {
             try {
                 userRepository.updateUserPhone(userId, phone)
                 success.postValue(true)
             }catch (e : Exception){
                 success.postValue(false)
             }
         }
    }

     fun uploadImage(inputStream: InputStream?,userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap())
                val imageUrl = result.get("secure_url") as String
                val publicId = result.get("public_id") as String
                deleteOldImage(userId)
                userRepository.updateUserImage(userId,imageUrl,publicId)
                url.postValue(imageUrl)
                isImageAdded.postValue(true)
            } catch (e: Exception) {
                Log.e("TAG", "uploadImage: error $e", )
                isImageAdded.postValue(false)
                url.postValue("")
            }
        }
    }

     fun getUserInformation(userId: String) {
         viewModelScope.launch {
             try {
                val userInfo = userRepository.getUser(userId)
                 userInfoItem.postValue(userInfo)
             }catch (e : Exception){
                 userInfoItem.postValue(null)
             }
        }
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
}