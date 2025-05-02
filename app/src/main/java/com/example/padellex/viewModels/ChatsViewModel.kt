package com.example.padellex.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padellex.Repositories.ChatsRepository
import com.example.padellex.Repositories.UserRepository
import com.example.padellex.model.MessageData
import com.example.padellex.model.UserChatsData
import com.example.padellex.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(private val userRepository: UserRepository , private val chatsRepository: ChatsRepository , private val auth : FirebaseAuth) : ViewModel(){
    val usersList = MutableLiveData<List<UserChatsData?>?>()
    val messagesList = MutableLiveData<List<MessageData>>()

    fun getUserByPhone(phone : String){
        userRepository.getUserByPhone(phone){userId->
            if (userId != null){
                viewModelScope.launch {
                    val user = userRepository.getUser(userId)
                    val list = mutableListOf(UserChatsData(chatId = listOf(auth.currentUser!!.uid,userId).sorted().joinToString("_"), imageUrl = user?.imageUrl , firstName = user!!.firstName , lastName = user.lastName , participants = listOf(auth.currentUser!!.uid,userId).sorted()))
                    usersList.postValue(list)
                }
            }else{
                usersList.postValue(null)
            }
        }
    }

    fun sendMessage(receiverId : String , text : String , senderData: UserChatsData, receiverData: UserChatsData){
        chatsRepository.sendMessage(auth.currentUser!!.uid,receiverId,text,senderData,receiverData)
    }

    fun getAllMessage(chatId : String){
        chatsRepository.getAllMessages(chatId){listOfMessages->
            messagesList.value = listOfMessages
        }
    }

    fun getAllChats(){
        chatsRepository.getAllUserChats(auth.currentUser!!.uid) { chats ->
            usersList.value = chats
        }
    }

    suspend fun getUserById(userId: String): UserInfo? {
        return userRepository.getUser(userId)
    }
}