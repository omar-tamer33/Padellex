package com.example.padellexadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.padellexadmin.R
import com.example.padellexadmin.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class BottomSheetFragment(private val courtName : String? = null, private val courtLocation : String? = null, private val courtPrice : String? = null, private val courtLatitude : String? = null, private val courtLongitude : String? = null , private val onSaveClick : (courtName : String,courtLocation : String,courtPrice : String,courtLatitude : String,courtLongitude : String) -> Unit): BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetBinding.inflate(inflater,container,false)
        binding.courtNameEt.setText(courtName)
        binding.courtLocationEt.setText(courtLocation)
        binding.courtPriceEt.setText(courtPrice)
        binding.courtLatitudeEt.setText(courtLatitude)
        binding.courtLongitudeEt.setText(courtLongitude)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveBtn.setOnClickListener {
            val courtName = binding.courtNameEt.text.toString()
            val courtLocation = binding.courtLocationEt.text.toString()
            val courtPrice = binding.courtPriceEt.text.toString()
            val courtLatitude = binding.courtLatitudeEt.text.toString()
            val courtLongitude = binding.courtLongitudeEt.text.toString()
            if (courtName.isNotBlank() && courtLocation.isNotBlank() && courtPrice.isNotBlank() && courtLatitude.isNotBlank() && courtLongitude.isNotBlank()) {
                onSaveClick(courtName,courtLocation,courtPrice,courtLatitude,courtLongitude)
                dismiss()
            }else{
                if (courtName.isBlank()){binding.courtNameLayout.error = getString(R.string.required)}
                if (courtLocation.isBlank()){binding.courtLocationLayout.error = getString(R.string.required)}
                if (courtPrice.isBlank()){binding.courtPriceLayout.error = getString(R.string.required)}
                if (courtLatitude.isBlank()){binding.courtLatitudeLayout.error = getString(R.string.required)}
                if (courtLongitude.isBlank()){binding.courtLongitudeLayout.error = getString(R.string.required)}
            }
        }
    }


}