package com.example.padellex.Repositories

import android.util.Log
import com.example.padellex.model.UserInfo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(db : FirebaseDatabase) {

  private  val userRef = db.getReference("Users Information")

    suspend fun addUser(user: UserInfo) : Boolean{
        return try {
            userRef.child(user.id!!).setValue(user).await()
            true
        }catch (e : Exception){
            false
        }
    }

     suspend fun getUser(id : String) : UserInfo?{
         try {
        val snapShot = userRef.child(id).get().await()
           val user = snapShot.getValue(UserInfo::class.java)
            return user
        }catch (e : Exception){
             Log.e("TAG", "getUser: error $e", )
             return null
        }
    }

     fun getUserPublicId(id : String,onComplete : (String?) -> Unit) {
         userRef.child(id).child("publicId").get().addOnSuccessListener { snapShot ->
             val publicId = snapShot.getValue(String::class.java)
             onComplete(publicId)
         }.addOnFailureListener { e ->
             Log.e("TAG", "getUserPublicId: error $e",)
             onComplete(null)
         }
     }

         fun updateUserImage(id: String, imageUrl: String, publicId: String): Boolean {
             try {
                 val updates = mapOf(
                     "imageUrl" to imageUrl,
                     "publicId" to publicId
                 )
                 userRef.child(id).updateChildren(updates)
                 return true
             } catch (e: Exception) {
                 Log.e("TAG", "updateUserImage: error $e",)
                 return false
             }
         }

         suspend fun updateUserPhone(id: String, newPhone: String): Boolean {
             try {
                 userRef.child(id).child("phone").setValue(newPhone).await()
                 return true
             } catch (e: Exception) {
                 Log.e("TAG", "updateUserPhone: error $e",)
                 return false
             }
         }

       suspend fun incUserStrikes(userId : String) : Boolean{
           try {
               val snapShot = userRef.child(userId).child("strikesCount").get().await()
               Log.e("TAG", "incUserStrikes: $snapShot", )
               val currentStrikesCount = snapShot.getValue(Int::class.java)
               userRef.child(userId).child("strikesCount").setValue(currentStrikesCount?.inc()).await()
               return true
           }catch (e : Exception){
               Log.e("TAG", "userStrikes: error $e", )
               return false
           }
       }

    suspend fun checkUserStrikes(userId: String) : Int?{
        try {
            val snapShot = userRef.child(userId).child("strikesCount").get().await()
            val currentStrikesCount = snapShot.getValue(Int::class.java)
            Log.e("TAG", "checkUserStrikes: $currentStrikesCount", )
            return currentStrikesCount
        }catch (e : Exception){
            Log.e("TAG", "checkUserStrikes: error $e", )
            return null
        }
    }

    fun updateUserRate(userId: String, shotSpeed: String, playerSpeed: String, playStyle : String): Boolean {
        try {
            val updates = mapOf(
                "power" to shotSpeed,
                "speed" to playerSpeed,
                "playStyle" to playStyle
            )
            userRef.child(userId).updateChildren(updates)
            return true
        } catch (e: Exception) {
            Log.e("TAG", "updateUserImage: error $e",)
            return false
        }
    }

         suspend fun deleteUser(id: String) : Boolean{
             try {
                 userRef.child(id).removeValue().await()
                 return true
             }catch (e : Exception){
                 Log.e("TAG", "deleteUser: error $e", )
                 return false
             }
         }
     }