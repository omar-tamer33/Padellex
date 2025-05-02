package com.example.padellex.Repositories

import android.util.Log
import com.example.padellex.model.MessageData
import com.example.padellex.model.UserChatsData
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ChatsRepository @Inject constructor(db : FirebaseDatabase) {
    val chatsRef = db.getReference("Chats")
    val userChatsRef = db.getReference("userChats")

    fun sendMessage(senderId: String, receiverId: String, text: String, receiverData: UserChatsData, senderData: UserChatsData) {
        val chatId = listOf(senderId, receiverId).sorted().joinToString("_")
        val message = mapOf(
            "senderId" to senderId,
            "text" to text,
            "timestamp" to ServerValue.TIMESTAMP
        )

        userChatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val senderChatMetadata = mapOf(
                    "participants" to listOf(senderId, receiverId).sorted(),
                    "lastMessage" to text,
                    "timestamp" to ServerValue.TIMESTAMP,
                    "imageUrl" to senderData.imageUrl,
                    "firstName" to senderData.firstName,
                    "lastName" to senderData.lastName,
                    "chatId"   to senderData.chatId
                )

                val receiverChatMetadata = mapOf(
                    "participants" to listOf(senderId, receiverId).sorted(),
                    "lastMessage" to text,
                    "timestamp" to ServerValue.TIMESTAMP,
                    "imageUrl" to receiverData.imageUrl,
                    "firstName" to receiverData.firstName,
                    "lastName" to receiverData.lastName,
                    "chatId"   to receiverData.chatId
                )

                userChatsRef.child(senderId).child(chatId).setValue(senderChatMetadata)
                userChatsRef.child(receiverId).child(chatId).setValue(receiverChatMetadata)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "Failed to fetch sender data: $error")
            }
        })

        chatsRef.child(chatId).push().setValue(message)
            .addOnFailureListener { e -> Log.e("TAG", "sendMessage error: $e") }
    }

    fun getAllUserChats(userId: String, onComplete: (List<UserChatsData>) -> Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = mutableListOf<UserChatsData>()
                for (child in snapshot.children) {
                    child.getValue(UserChatsData::class.java)?.let { chats.add(it) }
                }
                onComplete(chats)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatsRepository", "getAllUserChats cancelled: ${error.message}")
            }
        }
        userChatsRef.child(userId).addValueEventListener(listener)
    }

    fun getAllMessages(chatId : String , onComplete : (List<MessageData>) -> Unit){
        val list = mutableListOf<MessageData>()
        chatsRef.child(chatId).addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(MessageData::class.java)
                    if (message != null) {
                        list.add(message)
                    }
                onComplete(list)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: error $error")
            }

        })
    }
}