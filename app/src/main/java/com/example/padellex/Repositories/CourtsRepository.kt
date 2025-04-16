package com.example.padellex.Repositories

import com.example.padellex.model.CourtItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class CourtsRepository @Inject constructor(db : FirebaseDatabase) {
    val courtRef = db.getReference("Court Information")

    fun getAllCourts(onComplete: (List<CourtItem>) -> Unit){
        courtRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val courtList = mutableListOf<CourtItem>()
                for (child in snapshot.children){
                    child.getValue(CourtItem::class.java)?.let { courtList.add(it) }
                }
                onComplete(courtList)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(emptyList())
            }

        })
    }

    fun getCourtById(courtId : String , onComplete: (CourtItem?) -> Unit){
        courtRef.child(courtId).get().addOnSuccessListener { snapShot ->
            val courtItem = snapShot.getValue(CourtItem::class.java)
            onComplete(courtItem)
        }.addOnFailureListener {
            onComplete(null)
        }
    }
}