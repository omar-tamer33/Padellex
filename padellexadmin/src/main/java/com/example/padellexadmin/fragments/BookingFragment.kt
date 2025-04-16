package com.example.padellexadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.padellexadmin.Adapters.OnDeleteClickListener
import com.example.padellexadmin.Adapters.UserBookingAdapter
import com.example.padellexadmin.Repositories.TimeSlotsRepository
import com.example.padellexadmin.Repositories.UserBookingRepository
import com.example.padellexadmin.databinding.FargmentBookingBinding
import com.example.padellexadmin.model.UserBookingItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BookingFragment : Fragment() {
    @Inject lateinit var userBookingRepository: UserBookingRepository
    @Inject lateinit var timeSlotsRepository: TimeSlotsRepository
    lateinit var binding : FargmentBookingBinding
    val userBookingList = mutableListOf<UserBookingItem>()
    lateinit var adapter: UserBookingAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FargmentBookingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = UserBookingAdapter(userBookingList)
        binding.recyclerView.adapter = adapter
        getUserBookingData()
        adapter.deleteBtnClickListener = object : OnDeleteClickListener {
            override fun onDeleteClick(userBookingItem: UserBookingItem, position: Int) {
                timeSlotsRepository.unBookTimeSlot(userBookingItem)
                userBookingRepository.deleteUserBooking(userBookingItem.userId,userBookingItem.bookingId)
                userBookingList.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }
    }

    private fun getUserBookingData() {
        userBookingRepository.getAllBookings() { list ->
            if (list.isNotEmpty()) {
                userBookingList.clear()
                userBookingList.addAll(list)
                adapter.notifyDataSetChanged()
            } else {
                userBookingList.clear()
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "no booking found!", Toast.LENGTH_LONG).show()
            }
        }
    }
}