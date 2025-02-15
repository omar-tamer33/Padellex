package com.example.padellex.Dao

import com.example.padellex.CourtItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CourtsDao(db : FirebaseDatabase) {
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
}