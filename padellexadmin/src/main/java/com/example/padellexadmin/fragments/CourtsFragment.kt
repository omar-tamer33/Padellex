package com.example.padellexadmin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.padellexadmin.Adapters.CourtAdapter
import com.example.padellexadmin.Repositories.CourtsRepository
import com.example.padellexadmin.activities.CourtDetailsActivity
import com.example.padellexadmin.databinding.FragmentCourtsBinding
import com.example.padellexadmin.model.CourtData
import com.google.firebase.database.FirebaseDatabase

class CourtsFragment : Fragment() {
    lateinit var binding: FragmentCourtsBinding
    lateinit var adapter: CourtAdapter
    val db = FirebaseDatabase.getInstance()
    val courtsRepository = CourtsRepository(db)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCourtsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CourtAdapter(mutableListOf())
        binding.recyclerView.adapter = adapter
        getCourtsData()

    }

    fun getCourtsData(){
        courtsRepository.getAllCourts {courtItems ->
            if (courtItems.isNotEmpty()) {
                adapter.updateAdapter(courtItems)
                adapter.courtItemClickListener = object : CourtAdapter.onCourtItemClickListener{
                    override fun onCourtItemClick(courtData: CourtData, position: Int) {
                        val intent = Intent(requireContext(), CourtDetailsActivity::class.java)
                        intent.putExtra("courtData",courtData)
                        startActivity(intent)
                    }
                }
                adapter.deleteItemClickListener = object : CourtAdapter.onDeleteItemClickListener{
                    override fun onDeleteClick(courtData: CourtData, position: Int) {
                        courtsRepository.deleteCourt(courtData.id){}
                    }

                }
            }else{
                Toast.makeText(requireContext(),"no courts found", Toast.LENGTH_LONG).show()
            }
        }
    }
}