package com.example.padellex.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(val auth: FirebaseAuth) : ViewModel() {
    val message = MutableLiveData<String>()
    val isUserVerified = MutableLiveData<Boolean>()


    fun signOut(){
        auth.signOut()
    }

    fun checkIsUserVerified(){
        val user = auth.currentUser
        user?.reload()?.addOnCompleteListener {
            if (user.isEmailVerified){
                isUserVerified.value = true
            }else{
                isUserVerified.value = false
            }
        }
    }

     fun sendEmailVerification(){
        val user = auth.currentUser
        if (user != null){
            if (!user.isEmailVerified){
                user.sendEmailVerification().addOnSuccessListener {
                    message.value = "Email verification sent"
                }
            }
        }
    }
}