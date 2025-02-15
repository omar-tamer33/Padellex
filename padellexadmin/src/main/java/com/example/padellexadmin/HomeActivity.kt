package com.example.padellexadmin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.padellexadmin.Dao.CourtsDao
import com.example.padellexadmin.databinding.ActivityHomeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    val databaseReference = FirebaseDatabase.getInstance()
    val courtsDao = CourtsDao(databaseReference)
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
            addCourtInDatabase(courtName,courtLocation,courtPrice,courtLatitude,courtLongitude,id)
        }
    }

    fun addCourtInDatabase(courtName : String , courtLocation : String, courtPrice : Double, courtLatitude : Double , courtLongitude : Double, id : String){
        val courtData = CourtData(courtName = courtName, courtLocation = courtLocation, courtPrice = courtPrice, courtAvailability = false , id = id , latitude = courtLatitude , longitude = courtLongitude)
        courtsDao.addCourt(courtData){ success ->
            if (success){
                Toast.makeText(this,"court added successfully",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"failed to add court",Toast.LENGTH_LONG).show()
            }
        }
    }
}