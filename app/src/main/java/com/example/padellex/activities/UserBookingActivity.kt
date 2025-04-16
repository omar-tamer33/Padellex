package com.example.padellex.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.padellex.Adapters.OnDeleteClickListener
import com.example.padellex.Adapters.UserBookingAdapter
import com.example.padellex.Repositories.TimeSlotsRepository
import com.example.padellex.Repositories.UserBookingRepository
import com.example.padellex.databinding.ActivityUserBookingBinding
import com.example.padellex.model.UserBookingItem
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserBookingActivity : AppCompatActivity() {
    @Inject lateinit var userBookingRepository: UserBookingRepository
    @Inject lateinit var timeSlotsRepository: TimeSlotsRepository
    @Inject lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityUserBookingBinding
    val userBookingList = mutableListOf<UserBookingItem>()
    lateinit var adapter: UserBookingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = auth.currentUser!!.uid
        adapter = UserBookingAdapter(userBookingList)
        binding.userBookingRv.adapter = adapter
        getUserBookingData(userId)
        adapter.deleteBtnClickListener = object : OnDeleteClickListener{
            override fun onDeleteClick(userBookingItem: UserBookingItem, position: Int) {
                timeSlotsRepository.unBookTimeSlot(userBookingItem)
                userBookingRepository.deleteUserBooking(userId,userBookingItem.bookingId)
                userBookingList.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }
    }

    private fun getUserBookingData(userId : String) {
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