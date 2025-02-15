package com.example.padellex.Dao

import com.example.padellex.UserInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class UsersDao(db : FirebaseDatabase) {

  private  val userRef = db.getReference("Users Information")

     fun addUser(user: UserInfo , onComplete : (Boolean) -> Unit){
        userRef.child(user.id!!).setValue(user).addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener{
            onComplete(false)
        }
    }

     fun getUser(id : String , onComplete: (UserInfo?) -> Unit){
        userRef.child(id).get().addOnSuccessListener { snapShot ->
           val user = snapShot.getValue(UserInfo::class.java)
            onComplete(user)
        }.addOnFailureListener {
            onComplete(null)
        }
    }

     fun updateUserPhone(id : String ,newPhone : String , onComplete: (Boolean) -> Unit){
        userRef.child(id).child("phone").setValue(newPhone).addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }

     fun deleteUser(id : String , onComplete: (Boolean) -> Unit){
        userRef.child(id).removeValue().addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }

     fun getAllUsers(onComplete: (List<UserInfo>) -> Unit){
            userRef.get().addOnSuccessListener { snapShot ->
                val userList = mutableListOf<UserInfo>()
                for (child in snapShot.children) {
                    child.getValue(UserInfo::class.java)?.let { userList.add(it) }
                }
                onComplete(userList)
            }.addOnFailureListener{
                onComplete(emptyList())
            }
    }
  }