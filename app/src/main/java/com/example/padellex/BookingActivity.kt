package com.example.padellex

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.padellex.databinding.ActivityBookingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class BookingActivity : AppCompatActivity() {
    lateinit var binding: ActivityBookingBinding
    lateinit var databaseReference: DatabaseReference
    var courtItem: CourtItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courtItem = intent.getParcelableExtra("court")

        binding.mapBtn.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("court", courtItem)
            startActivity(intent)
        }

        binding.bookBtn.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val courtName = courtItem?.courtName
            val courtId = courtItem?.id
            val userId = user?.uid
            val userName = user?.displayName

            if (userId != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    val phone = getUserPhoneNumber(userId)
                    val bookingId = UUID.randomUUID().toString()
                    databaseReference = FirebaseDatabase.getInstance().getReference("Booking Information")
                    val bookingData = BookingData(courtName, courtId, userId, bookingId, userName, phone)

                    databaseReference.child(bookingId).setValue(bookingData)
                        .addOnSuccessListener {
                            Toast.makeText(this@BookingActivity, "Booking successful", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@BookingActivity, "Booking failed", Toast.LENGTH_LONG).show()
                        }
                }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
            }
        }
    }
    private suspend fun getUserPhoneNumber(userId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = FirebaseDatabase.getInstance().getReference("Users Information")
                    .child(userId).child("phone").get().await()
                snapshot.getValue(String::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}