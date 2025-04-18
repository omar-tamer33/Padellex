package com.example.padellex.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padellex.Repositories.UserRepository
import com.example.padellex.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(val userRepository: UserRepository , val auth: FirebaseAuth) : ViewModel() {
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val isUserRegistered = MutableLiveData(false)

    fun userRegister(email : String , password : String , firstName : String , lastName : String , phone : String){
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                val user = auth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName("$firstName $lastName")
                    .build()
                user?.updateProfile(profileUpdates)?.await()
                if (user != null) {
                    saveUserInfoInDatabase(user.uid, firstName, lastName, phone)
                }
            } catch (e: Exception) {
                errorMessage.postValue(e.message ?: "Unknown error")
            }

        }
    }

     private suspend fun saveUserInfoInDatabase(id : String, firstName : String, lastName : String, phone : String){
            try {
                val success = userRepository.addUser(UserInfo(id = id, firstName = firstName, lastName = lastName, phone =phone))
                if (success){
                    successMessage.postValue("User Added Successfully")
                    isUserRegistered.postValue(true)
                }else{
                    errorMessage.postValue("Failed to add user")
                }
            }catch (e : Exception){
                errorMessage.postValue(e.message ?: "Unknown error")
            }
        }
    }