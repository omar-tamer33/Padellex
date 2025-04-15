package com.example.padellex.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.padellex.Adapters.OnDeleteClickListener
import com.example.padellex.Adapters.UserBookingAdapter
import com.example.padellex.R
import com.example.padellex.Repositories.CourtsRepository
import com.example.padellex.Repositories.TimeSlotsRepository
import com.example.padellex.Repositories.UserBookingRepository
import com.example.padellex.Repositories.UserRepository
import com.example.padellex.databinding.ActivityUserBookingBinding
import com.example.padellex.model.UserBookingItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class UserBookingActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserBookingBinding
    val db = FirebaseDatabase.getInstance()
    val userBookingRepository = UserBookingRepository(db)
    val timeSlotsRepository = TimeSlotsRepository(db)
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser!!.uid
    val userBookingList = mutableListOf<UserBookingItem>()
    lateinit var adapter: UserBookingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = UserBookingAdapter(userBookingList)
        binding.userBookingRv.adapter = adapter
        getUserBookingData()
        adapter.deleteBtnClickListener = object : OnDeleteClickListener{
            override fun onDeleteClick(userBookingItem: UserBookingItem, position: Int) {
                timeSlotsRepository.unBookTimeSlot(userBookingItem)
                userBookingRepository.deleteUserBooking(userId,userBookingItem.bookingId)
                userBookingList.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }
    }

    private fun getUserBookingData() {
        userBookingRepository.getAllUserBooking(userId) { list ->
            if (list.isNotEmpty()) {
                userBookingList.clear()
                userBookingList.addAll(list)
                adapter.notifyDataSetChanged()
            } else {
                userBookingList.clear()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "no booking found!", Toast.LENGTH_LONG).show()
            }
        }
    }
}