package com.example.padellex.Fragments

import android.Manifest
import android.content.Intent
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.padellex.activities.LoginActivity
import com.example.padellex.R
import com.example.padellex.databinding.FragmentProfileBinding
import com.example.padellex.viewModels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    val viewModel : ProfileViewModel by viewModels()
    @Inject lateinit var auth : FirebaseAuth
    lateinit var binding: FragmentProfileBinding
    private val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            viewModel.uploadImage(inputStream,auth.currentUser!!.uid)
        } else {
            Toast.makeText(requireContext(),"Select image to add",Toast.LENGTH_LONG).show()
        }
    }
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
               updateProfileImage()
            } else {
               showRationalDialog()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
       viewModel.getUserInformation(auth.currentUser!!.uid)

        binding.saveBtn.setOnClickListener {
            val phone = binding.userPhoneEt.text.toString()
            val userId = auth.currentUser!!.uid
            viewModel.updateUserInformation(userId,phone)
        }

        binding.signoutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.profileImage.setOnClickListener {
            checkPermissionGranted(storagePermission)
        }
    }

    private fun showRationalDialog() {
        dialog(title = "Why we need this permission ?" , message = "We need this permission to update profile image" , positiveBtnText = "yes, i understand", onPositiveClick = {
            requestPermissionLauncher.launch(storagePermission)
        }, negativeBtnText = "no, i refuse")
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

    private fun updateProfileImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }





    private fun checkPermissionGranted(permission : String){
       when{
           ContextCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED -> {
               updateProfileImage()
           }
           ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),permission) -> {
               showRationalDialog()
           }
           else -> {
               requestPermissionLauncher.launch(permission)
           }
       }
    }



    private fun observeViewModel(){
        viewModel.success.observe(viewLifecycleOwner){success->
            if (success){
                Toast.makeText(requireContext(),"your information updated successfully", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(requireContext(),"Failed to update information!", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isImageAdded.observe(viewLifecycleOwner){imageAdded->
            if (imageAdded) {
                Toast.makeText(requireContext(),"Image added successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(),"Failed to upload image", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.url.observe(viewLifecycleOwner){imageUrl->
            if (isAdded) {
                Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.blank_profile_picture)
                    .into(binding.profileImage)
            }
        }

        viewModel.userInfoItem.observe(viewLifecycleOwner){userInfo->
            userInfo?.let {
                val firstName = userInfo.firstName
                val lastName = userInfo.lastName
                val phone = userInfo.phone
                val imageUrl = userInfo.imageUrl

                binding.userEmailTv.text = auth.currentUser!!.email.toString()
                binding.userNameTv.text = "$firstName $lastName"
                binding.userPhoneEt.setText(phone)
                if (isAdded) {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.blank_profile_picture)
                        .into(binding.profileImage)
                }
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "Failed to retrieve user info",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
