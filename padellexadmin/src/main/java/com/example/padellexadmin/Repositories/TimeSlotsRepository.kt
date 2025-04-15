package com.example.padellexadmin.Repositories

import android.util.Log
import com.example.padellexadmin.model.TimeSlot
import com.example.padellexadmin.model.UserBookingItem
import com.example.padellexadmin.utilities.TimeSlotUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TimeSlotsRepository(db : FirebaseDatabase) {
    val bookingRef = db.getReference("Booking Information")

    fun timeSlotAvailability(courtId: String , dateStr: String , timeSlot: TimeSlot){
        bookingRef.child(courtId).child(dateStr).child(timeSlot.timeKey).child("booked").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentBookedValue = snapshot.getValue(Boolean::class.java) ?: false
                val newBookedValue = !currentBookedValue
                val update = mapOf(
                    "booked" to newBookedValue,
                    "userId" to "Admin"
                )
                bookingRef.child(courtId).child(dateStr).child(timeSlot.timeKey).updateChildren(update)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled: $error", )
            }
        })
    }

    fun getTimeSlots(courtId: String , dateStr: String , onComplete: (MutableList<TimeSlot>) -> Unit){
        bookingRef.child(courtId).child(dateStr).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val timeSlotList = mutableListOf<TimeSlot>()
                for (child in snapshot.children){
                    val timeKey = child.key ?: continue
                    val slot = child.getValue(TimeSlot::class.java)
                    if (slot != null) {
                        timeSlotList.add(slot.copy(timeKey = timeKey))
                    }
                }
                onComplete(timeSlotList)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(mutableListOf())
            }

        })
    }

    fun generateTodaySlotsIfNeeded(courtId: String , dateStr: String) {
        val todayRef = bookingRef.child(courtId).child(dateStr)
        todayRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    val slots = TimeSlotUtils.generateTimeSlotsMapForDate(dateStr)
                    todayRef.setValue(slots)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun unBookTimeSlot(userBookingItem: UserBookingItem) {
        val update = mapOf(
            "booked" to false,
            "userId" to null
        )
        for (timeKey in userBookingItem.bookingTime.orEmpty()) {
            bookingRef
                .child(userBookingItem.courtId)
                .child(userBookingItem.bookingDate)
                .child(timeKey)
                .updateChildren(update)
                .addOnFailureListener { e ->
                    Log.e("TAG", "Failed to unbook time slot: $e")
                }
        }
    }
}