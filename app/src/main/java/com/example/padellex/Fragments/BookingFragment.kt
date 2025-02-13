package com.example.padellex.Fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.padellex.Adapters.CourtAdapter
import com.example.padellex.BookingActivity
import com.example.padellex.CourtItem
import com.example.padellex.databinding.FragmentBookingBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class BookingFragment : Fragment() {
    lateinit var binding: FragmentBookingBinding
    lateinit var mutableList: MutableList<CourtItem>
    lateinit var adapter: CourtAdapter
    lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookingBinding.inflate(inflater)
        mutableList = mutableListOf()
        getCourtData()
        adapter = CourtAdapter(mutableList)
        binding.recyclerView.adapter = adapter
        adapter.courtItemClickListener = object : CourtAdapter.onCourtItemClickListener{
            override fun onCourtItemClick(courtItem: CourtItem, position: Int) {
                val intent : Intent = Intent(activity,BookingActivity::class.java)
                intent.putExtra("court",courtItem)
                startActivity(intent)
            }

        }
        return binding.root
    }

    private fun getCourtData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Court Information")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    mutableList.clear()
                    for (courtSnapshot in dataSnapshot.children) {
                        val court = courtSnapshot.getValue(CourtItem::class.java)
                        court?.let { mutableList.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}