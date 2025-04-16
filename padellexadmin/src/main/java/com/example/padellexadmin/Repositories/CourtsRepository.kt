package com.example.padellexadmin.Repositories

import com.example.padellexadmin.model.CourtData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class CourtsRepository @Inject constructor(db : FirebaseDatabase) {
    val courtRef = db.getReference("Court Information")

    fun addCourt(court: CourtData, onComplete: (Boolean) -> Unit) {
        courtRef.child(court.id).setValue(court).addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }

    fun deleteCourt(id: String, onComplete: (Boolean) -> Unit) {
        courtRef.child(id).removeValue().addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }

    fun updateCourt(court: CourtData, onComplete: (Boolean) -> Unit) {
        courtRef.child(court.id).setValue(court).addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }

    fun getCourtDetails(courtId: String, onComplete: (Boolean) -> Unit) {
        courtRef.child(courtId).get().addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }


    fun getAllCourts(onComplete: (MutableList<CourtData>) -> Unit) {
        courtRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courtList = mutableListOf<CourtData>()
                for (child in snapshot.children) {
                    child.getValue(CourtData::class.java)?.let { courtList.add(it) }
                }
                onComplete(courtList)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(mutableListOf())
            }

        })
    }
}