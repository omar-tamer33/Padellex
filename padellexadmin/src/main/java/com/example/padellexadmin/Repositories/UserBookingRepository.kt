package com.example.padellexadmin.Repositories

import android.content.Context
import android.util.Log
import com.example.padellexadmin.model.UserBookingItem
import com.example.padellexadmin.utilities.NotificationUtils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class UserBookingRepository @Inject constructor(db : FirebaseDatabase) {
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

    fun bookingNotification(context: Context) {
        userBookingRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val userId = snapshot.key
                val userBookingsRef = userBookingRef.child(userId!!)

                userBookingsRef.addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(bookingSnapshot: DataSnapshot, p1: String?) {
                        val booking = bookingSnapshot.getValue(UserBookingItem::class.java)
                        booking?.let {
                            NotificationUtils.showLocalNotification(
                                context,
                                "New Booking at ${it.courtName}, ${it.courtLocation} for ${it.courtPrice}"
                            )
                        }
                    }

                    override fun onChildChanged(bookingSnapshot: DataSnapshot, p1: String?) {
                    }

                    override fun onChildRemoved(bookingSnapshot: DataSnapshot) {
                        val booking = bookingSnapshot.getValue(UserBookingItem::class.java)
                        booking?.let {
                            NotificationUtils.showLocalNotification(
                                context,
                                "Booking Cancelled at ${it.courtName}, ${it.courtLocation}"
                            )
                        }
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}