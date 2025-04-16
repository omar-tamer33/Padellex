package com.example.padellexadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.padellexadmin.Repositories.CourtsRepository
import com.example.padellexadmin.databinding.BottomSheetBinding
import com.example.padellexadmin.model.CourtData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetFragment: BottomSheetDialogFragment() {
    @Inject lateinit var courtsRepository: CourtsRepository
    lateinit var binding: BottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.savebtn.setOnClickListener {
            val courtName = binding.courtnameEt.text.toString()
            val courtLocation = binding.courtlocationEt.text.toString()
            val courtPrice : Double = binding.courtpriceEt.text.toString().toDouble()
            val courtLatitude : Double= binding.courtLatitudeEt.text.toString().toDouble()
            val courtLongitude : Double = binding.courtLongitudeEt.text.toString().toDouble()
            val id = UUID.randomUUID().toString()
            addCourtInDatabase(courtName,courtLocation,courtPrice,courtLatitude,courtLongitude,id)
            dismiss()
        }
    }

    fun addCourtInDatabase(courtName : String , courtLocation : String, courtPrice : Double, courtLatitude : Double , courtLongitude : Double, id : String){
        val courtData = CourtData(courtName = courtName, courtLocation = courtLocation, courtPrice = courtPrice, courtAvailability = false , id = id , latitude = courtLatitude , longitude = courtLongitude)
        courtsRepository.addCourt(courtData){}
    }
}