package com.example.padellexadmin.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.padellexadmin.R
import com.example.padellexadmin.databinding.BottomSheetBinding
import com.example.padellexadmin.model.CourtData
import com.example.padellexadmin.viewModels.CourtDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetFragment(private val courtData: CourtData?,courtId : String, private val onSaveClick : (courtName : String,courtLocation : String,courtPrice : String,courtLatitude : String,courtLongitude : String) -> Unit): BottomSheetDialogFragment() {
    val viewModel : CourtDetailsViewModel by viewModels()
    lateinit var binding: BottomSheetBinding
    private val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            if (courtData != null) {
                viewModel.uploadImage(inputStream, courtId)
            }else{
                Toast.makeText(requireContext(),"Fill the data first", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(),"Select image to add", Toast.LENGTH_LONG).show()
        }
    }
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                updateCourtImage()
            } else {
                showRationalDialog()
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetBinding.inflate(inflater,container,false)
        if (courtData != null) {
            binding.courtNameEt.setText(courtData.courtName)
            binding.courtLocationEt.setText(courtData.courtLocation)
            binding.courtPriceEt.setText(courtData.courtPrice.toString())
            binding.courtLatitudeEt.setText(courtData.latitude.toString())
            binding.courtLongitudeEt.setText(courtData.longitude.toString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        binding.courtImage.setOnClickListener {
            checkPermissionGranted(storagePermission)
        }

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
    

    private fun updateCourtImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showRationalDialog() {
        dialog(title = "Why we need this permission ?" , message = "We need this permission to update court image" , positiveBtnText = "yes, i understand", onPositiveClick = {
            requestPermissionLauncher.launch(storagePermission)
        }, negativeBtnText = "no, i refuse")
    }

    private fun checkPermissionGranted(permission : String){
        when{
            ContextCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED -> {
                updateCourtImage()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),permission) -> {
                showRationalDialog()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun dialog(title: String, message: String, positiveBtnText: String, onPositiveClick: () -> Unit?, negativeBtnText: String? = null, onNegativeClick: (() -> Unit?)? = null) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(title).setMessage(message).setPositiveButton(positiveBtnText){ dialog , which ->
            onPositiveClick()
            dialog.dismiss()
        }
        if (negativeBtnText != null) {
            alertDialog.setNegativeButton(negativeBtnText) { dialog , which ->
                onNegativeClick?.invoke()
                dialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun observeViewModel(){
        viewModel.isImageAdded.observe(viewLifecycleOwner){imageAdded->
            if (imageAdded) {
                Toast.makeText(requireContext(),"Image added successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(),"Failed to upload image", Toast.LENGTH_LONG).show()
            }
        }
    }
}