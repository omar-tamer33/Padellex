package com.example.padellex.Repositories

import android.util.Log
import com.example.padellex.model.UserBookingItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class UserBookingRepository @Inject constructor(db : FirebaseDatabase) {
    val userBookingRef = db.getReference("User Booking")

     fun addUserBooking(userId : String, userBookingItem: UserBookingItem) {
        userBookingRef.child(userId).child(userBookingItem.bookingId).setValue(userBookingItem)
            .addOnSuccessListener {
                Log.e("TAG", "user booking added ",)
            }.addOnFailureListener {
            Log.e("TAG", "user booking failed ",)
        }
    }



    fun deleteUserBooking(userId: String,bookingId : String){
        userBookingRef.child(userId).child(bookingId).removeValue().addOnSuccessListener {
            Log.e("TAG", "deleteUserBooking: user booking removed successfully")
        }.addOnFailureListener {
            Log.e("TAG", "deleteUserBooking: user booking failed to remove", )
        }
    }

    fun deleteAllUserBooking(userId: String){
        userBookingRef.child(userId).removeValue().addOnSuccessListener {
            Log.e("TAG", "deleteUserBooking: user booking removed successfully")
        }.addOnFailureListener {
            Log.e("TAG", "deleteUserBooking: user booking failed to remove", )
        }
    }

     fun getAllUserBookingByDate(userId: String,dateStr : String,onComplete : (List<UserBookingItem>) -> Unit){
            val userBookingItemList = mutableListOf<UserBookingItem>()
            userBookingRef.child(userId).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    userBookingItemList.clear()
                    for (child in snapshot.children) {
                        child.getValue(UserBookingItem::class.java).let {
                            if (it != null && it.bookingDate == dateStr) {
                                userBookingItemList.add(it)
                            }
                        }
                    }
                    Log.e("TAG", "onDataChange: $userBookingItemList",)
                    onComplete(userBookingItemList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "onCancelled: failed to get all user booking $error")
                }

            })
     }
}