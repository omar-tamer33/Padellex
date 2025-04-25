package com.example.padellex.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.padellex.Adapters.CourtAdapter
import com.example.padellex.activities.BookingActivity
import com.example.padellex.model.CourtItem
import com.example.padellex.databinding.FragmentBookingBinding
import com.example.padellex.viewModels.CourtsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourtsFragment : Fragment() {
    val viewModel : CourtsViewModel by viewModels()
    lateinit var binding: FragmentBookingBinding
    lateinit var mutableList: MutableList<CourtItem>
    lateinit var adapter: CourtAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookingBinding.inflate(inflater)
        mutableList = mutableListOf()
        adapter = CourtAdapter(mutableList)
        binding.recyclerView.adapter = adapter
        observeViewModel()
        viewModel.getCourtData()

        adapter.courtItemClickListener = object : CourtAdapter.onCourtItemClickListener{
            override fun onCourtItemClick(courtItem: CourtItem, position: Int) {
                navigateToBooking(courtItem)
            }

        }

        return binding.root
    }

    private fun observeViewModel(){
        viewModel.list.observe(viewLifecycleOwner){courtItemsList->
            if (courtItemsList.isNotEmpty()){
                hideLoading()
                mutableList.clear()
                mutableList.addAll(courtItemsList)
                adapter.notifyDataSetChanged()
            }else{
                showLoading()
            }
        }
    }

    fun navigateToBooking(courtItem: CourtItem){
        val intent = Intent(requireContext(), BookingActivity::class.java)
        intent.putExtra("court",courtItem)
        startActivity(intent)
    }

    private fun showLoading(){
        binding.recyclerView.isVisible = false
        binding.loading.isVisible = true
    }

    private fun hideLoading(){
        binding.recyclerView.isVisible = true
        binding.loading.isVisible = false
    }
}