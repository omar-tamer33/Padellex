package com.example.padellexadmin.Repositories

import android.util.Log
import com.example.padellexadmin.model.CourtData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class CourtsRepository @Inject constructor(db : FirebaseDatabase) {
    val courtRef = db.getReference("Court Information")

    fun addCourt(court: CourtData , onComplete: (Boolean) -> Unit) {
        courtRef.child(court.id).setValue(court).addOnSuccessListener {
            Log.e("TAG", "addCourt successfully ", )
            onComplete(true)
        }.addOnFailureListener {e->
            Log.e("TAG", "addCourt: error $e", )
            onComplete(false)
        }
    }

    fun deleteCourt(id: String) {
        courtRef.child(id).removeValue().addOnSuccessListener {
            Log.e("TAG", "deleteCourt successfully ", )
        }.addOnFailureListener {e ->
            Log.e("TAG", "deleteCourt: error $e", )
        }
    }

    fun getCourtPublicId(id : String,onComplete : (String?) -> Unit) {
        courtRef.child(id).child("publicId").get().addOnSuccessListener { snapShot ->
            val publicId = snapShot.getValue(String::class.java)
            onComplete(publicId)
        }.addOnFailureListener { e ->
            Log.e("TAG", "getUserPublicId: error $e",)
            onComplete(null)
        }
    }

    fun updateCourtImage(id: String, imageUrl: String, publicId: String): Boolean {
        try {
            val updates = mapOf(
                "imageUrl" to imageUrl,
                "publicId" to publicId
            )
            courtRef.child(id).updateChildren(updates)
            return true
        } catch (e: Exception) {
            Log.e("TAG", "updateUserImage: error $e",)
            return false
        }
    }

    fun updateCourt(court: CourtData, onComplete: (Boolean) -> Unit) {
        courtRef.child(court.id).setValue(court).addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }

    fun getCourtDetails(courtId: String, onComplete: (CourtData?) -> Unit) {
        courtRef.child(courtId).get().addOnSuccessListener {
            val courtData = it.getValue(CourtData::class.java)
            onComplete(courtData)
        }.addOnFailureListener {
            onComplete(null)
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