package com.example.padellex.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.padellex.Adapters.OnDeleteClickListener
import com.example.padellex.Adapters.UserBookingAdapter
import com.example.padellex.databinding.ActivityUserBookingBinding
import com.example.padellex.model.UserBookingItem
import com.example.padellex.viewModels.UserBookingViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserBookingActivity : AppCompatActivity() {
    val viewModel : UserBookingViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityUserBookingBinding
    val userBookingList = mutableListOf<UserBookingItem>()
    lateinit var adapter: UserBookingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
        val userId = auth.currentUser!!.uid
        adapter = UserBookingAdapter(userBookingList)
        binding.userBookingRv.adapter = adapter
        viewModel.getUserBookingData(userId)
        adapter.deleteBtnClickListener = object : OnDeleteClickListener{
            override fun onDeleteClick(userBookingItem: UserBookingItem, position: Int) {
                viewModel.onDeleteClick(userBookingItem)
            }
        }
    }

    private fun observeViewModel(){
        viewModel.bookingList.observe(this){list->
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

        viewModel.deleteBookingEvent.observe(this){userBookingItem ->
            val index = userBookingList.indexOf(userBookingItem)
            if (index != -1){
                userBookingList.removeAt(index)
                adapter.notifyItemRemoved(index)
                viewModel.preformDelete(userBookingItem,auth.currentUser!!.uid)
            }
        }
    }
}