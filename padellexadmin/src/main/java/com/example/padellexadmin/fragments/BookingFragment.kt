package com.example.padellexadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.padellexadmin.Adapters.OnDeleteClickListener
import com.example.padellexadmin.Adapters.UserBookingAdapter
import com.example.padellexadmin.databinding.FargmentBookingBinding
import com.example.padellexadmin.model.UserBookingItem
import com.example.padellexadmin.viewModels.BookingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingFragment : Fragment() {
    private val viewModel : BookingViewModel by viewModels()
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
        viewModel.getUserBookingData()
        observeViewModel()
        adapter.deleteBtnClickListener = object : OnDeleteClickListener {
            override fun onDeleteClick(userBookingItem: UserBookingItem, position: Int) {
               viewModel.onDeleteClick(userBookingItem)
            }
        }
    }

   private fun observeViewModel(){
       viewModel.list.observe(viewLifecycleOwner){list->
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

       viewModel.onDeleteEvent.observe(viewLifecycleOwner){userBookingItem->
           val index = userBookingList.indexOf(userBookingItem)
           if (index != -1) {
               userBookingList.removeAt(index)
               adapter.notifyItemRemoved(index)
               viewModel.preformDelete(userBookingItem)
           }
       }
   }
}