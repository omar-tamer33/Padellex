package com.example.padellex.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.padellex.Adapters.CourtAdapter
import com.example.padellex.BookingActivity
import com.example.padellex.CourtItem
import com.example.padellex.Dao.CourtsDao
import com.example.padellex.databinding.FragmentBookingBinding
import com.google.firebase.database.FirebaseDatabase

class BookingFragment : Fragment() {
    lateinit var binding: FragmentBookingBinding
    lateinit var mutableList: MutableList<CourtItem>
    lateinit var adapter: CourtAdapter
    val databaseReference = FirebaseDatabase.getInstance()
    val courtDao = CourtsDao(databaseReference)
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

        return binding.root
    }

    private fun getCourtData() {
        courtDao.getAllCourts { courtItems ->
            if (courtItems.isNotEmpty()){
                mutableList.clear()
                mutableList.addAll(courtItems)
                adapter = CourtAdapter(mutableList)
                binding.recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                adapter.courtItemClickListener = object : CourtAdapter.onCourtItemClickListener{
                    override fun onCourtItemClick(courtItem: CourtItem, position: Int) {
                        val intent : Intent = Intent(activity,BookingActivity::class.java)
                        intent.putExtra("court",courtItem)
                        startActivity(intent)
                    }

                }
            }else{
                Toast.makeText(requireContext(),"no Courts found",Toast.LENGTH_LONG).show()
            }
        }
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