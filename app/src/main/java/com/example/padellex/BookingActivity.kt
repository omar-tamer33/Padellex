package com.example.padellex

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.padellex.databinding.ActivityBookingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class BookingActivity : AppCompatActivity() {
    lateinit var binding: ActivityBookingBinding
    lateinit var databaseReference : DatabaseReference
    var courtItem : CourtItem?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courtItem = intent.getParcelableExtra("court")

        binding.mapBtn.setOnClickListener {
            val intent = Intent(this,MapActivity::class.java)
            intent.putExtra("court",courtItem)
            startActivity(intent)
        }

        binding.bookBtn.setOnClickListener { val user = FirebaseAuth.getInstance().currentUser
            val courtName = courtItem?.courtName
            val courtId = courtItem?.id
            val userId = user?.uid
            val userName = user?.displayName
            val bookingId = UUID.randomUUID().toString()

            databaseReference = FirebaseDatabase.getInstance().getReference("Booking Information")
            val bookingData = BookingData(courtName,courtId,userId,bookingId,userName)
            databaseReference.child(bookingId).setValue(bookingData).addOnSuccessListener {
                Toast.makeText(this,"success", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show()

            }
        }


    }
}