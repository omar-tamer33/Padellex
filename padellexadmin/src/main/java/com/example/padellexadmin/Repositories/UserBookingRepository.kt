package com.example.padellexadmin.Repositories

import android.util.Log
import com.example.padellexadmin.model.UserBookingItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserBookingRepository(db : FirebaseDatabase) {
    val userBookingRef = db.getReference("User Booking")


    fun deleteUserBooking(userId: String,bookingId : String){
        userBookingRef.child(userId).child(bookingId).removeValue().addOnSuccessListener {
            Log.e("TAG", "deleteUserBooking: user booking removed successfully")
        }.addOnFailureListener {
            Log.e("TAG", "deleteUserBooking: user booking failed to remove", )
        }
    }

    fun getAllBookings(onComplete : (List<UserBookingItem>) -> Unit) {
        val userBookingItemList = mutableListOf<UserBookingItem>()
        userBookingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userBookingItemList.clear()
                for (userSnapShot in snapshot.children){
                    for (bookingSnapShot in userSnapShot.children){
                      val booking = bookingSnapShot.getValue(UserBookingItem::class.java)
                        if (booking != null){
                            userBookingItemList.add(booking)
                        }
                    }
                }
                onComplete(userBookingItemList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled: cannot get all booking $error", )
            }

        })
    }
}