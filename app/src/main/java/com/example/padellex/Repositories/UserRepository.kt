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

         fun deleteUser(id: String, onComplete: (Boolean) -> Unit) {
             userRef.child(id).removeValue().addOnSuccessListener {
                 onComplete(true)
             }.addOnFailureListener {
                 onComplete(false)
             }
         }

         fun getAllUsers(onComplete: (List<UserInfo>) -> Unit) {
             userRef.get().addOnSuccessListener { snapShot ->
                 val userList = mutableListOf<UserInfo>()
                 for (child in snapShot.children) {
                     child.getValue(UserInfo::class.java)?.let { userList.add(it) }
                 }
                 onComplete(userList)
             }.addOnFailureListener {
                 onComplete(emptyList())
             }
         }
     }