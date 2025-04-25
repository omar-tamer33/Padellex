package com.example.padellexadmin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.padellexadmin.Adapters.CourtAdapter
import com.example.padellexadmin.activities.CourtDetailsActivity
import com.example.padellexadmin.databinding.FragmentCourtsBinding
import com.example.padellexadmin.model.CourtData
import com.example.padellexadmin.viewModels.CourtsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourtsFragment : Fragment() {
    private val viewModel : CourtsViewModel by viewModels()
    lateinit var binding: FragmentCourtsBinding
    lateinit var adapter: CourtAdapter
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
        adapter.courtItemClickListener = object : CourtAdapter.onCourtItemClickListener {
            override fun onCourtItemClick(courtData: CourtData, position: Int) {
               navigateToCourtDetails(courtData)
            }
        }

        adapter.deleteItemClickListener = object : CourtAdapter.onDeleteItemClickListener{
            override fun onDeleteClick(courtData: CourtData, position: Int) {
                viewModel.onDeleteClick(courtData)
            }
        }
        viewModel.getCourtsData()
        observeViewModel()

    }

    private fun observeViewModel(){
        viewModel.list.observe(viewLifecycleOwner){courtsList->
            if (courtsList.isNotEmpty()) {
                hideLoading()
                adapter.updateAdapter(courtsList)
            }else{
                showLoading()
            }
        }

        viewModel.onDeleteEvent.observe(viewLifecycleOwner){courtData->
            viewModel.preformDelete(courtData.id)
        }
    }

    private fun navigateToCourtDetails(courtData: CourtData){
        val intent = Intent(requireContext(), CourtDetailsActivity::class.java)
        intent.putExtra("courtData", courtData)
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