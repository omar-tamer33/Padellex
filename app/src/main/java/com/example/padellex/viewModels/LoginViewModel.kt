package com.example.padellex.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val auth: FirebaseAuth) : ViewModel() {
    val success = MutableLiveData<Boolean>()
    val isUserVerified = MutableLiveData<Boolean>()

    fun userLoginWithEmailAndPassword(email : String , password : String){
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email,password).await()
                checkUserLoginState()
                success.postValue(true)
            }catch (e: Exception){
                success.postValue(false)
            }
        }
    }

     fun checkUserLoginState() {
        val currentUser = auth.currentUser
        currentUser?.reload()?.addOnCompleteListener {
            if (currentUser.isEmailVerified) {
               isUserVerified.value = true
            } else if (!currentUser.isEmailVerified) {
               isUserVerified.value = false
            }
        }
    }
}