package com.example.padellexadmin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.padellexadmin.databinding.ActivityHomeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.savebtn.setOnClickListener {
            val courtName = binding.courtnameEt.text.toString()
            val courtLocation = binding.courtlocationEt.text.toString()
            val courtPrice : Double = binding.courtpriceEt.text.toString().toDouble()
            val courtLatitude : Double= binding.courtLatitudeEt.text.toString().toDouble()
            val courtLongitude : Double = binding.courtLongitudeEt.text.toString().toDouble()
            val id = UUID.randomUUID().toString()

            databaseReference = FirebaseDatabase.getInstance().getReference("Court Information")
            val CourtData = CourtData(courtName = courtName, courtLocation = courtLocation, courtPrice = courtPrice, courtAvailability = false , id = id , latitude = courtLatitude , longitude = courtLongitude)
            databaseReference.child(id.toString()).setValue(CourtData).addOnSuccessListener {
                Toast.makeText(this,"success",Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()

            }

        }
    }
}