package com.example.padellex.Fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import com.bumptech.glide.Glide
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.padellex.Repositories.UserRepository
import com.example.padellex.activities.LoginActivity
import com.example.padellex.R
import com.example.padellex.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    @Inject lateinit var auth : FirebaseAuth
    @Inject lateinit var userRepository: UserRepository
    lateinit var binding: FragmentProfileBinding
    lateinit var cloudinary : Cloudinary
    private val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            uploadImage(uri)
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
        initCloudinary()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       getUserInformation()

        binding.saveBtn.setOnClickListener {
            updateUserInformation()
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

    private fun uploadImage(uri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val userId = auth.currentUser?.uid.toString()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap())
                val imageUrl = result.get("secure_url") as String
                val publicId = result.get("public_id") as String
                deleteOldImage(userId)
                userRepository.updateUserImage(userId,imageUrl,publicId){ success ->
                        requireActivity().runOnUiThread {
                            if (success) {
                                Toast.makeText(requireContext(),"Image added successfully", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(requireContext(),"Failed to upload image", Toast.LENGTH_LONG).show()
                            }
                        }
                }
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        Glide.with(requireContext()).load(imageUrl)
                            .placeholder(R.drawable.blank_profile_picture)
                            .into(binding.profileImage)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteOldImage(userId : String){
        userRepository.getUserPublicId(userId){ publicId ->
            if (publicId != null){
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val options = mapOf("resource_type" to "image")
                        cloudinary.uploader().destroy(publicId, options)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
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

    private fun getUserInformation() {
        val userId = auth.currentUser?.uid ?: return
        userRepository.getUser(userId){ userInfo ->
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
                Toast.makeText(requireContext(), "Failed to retrieve user info", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserInformation(){
            val phone = binding.userPhoneEt.text.toString()
            val userId = auth.currentUser?.uid ?: return
        userRepository.updateUserPhone(userId,phone){ success ->
            if (success){
                Toast.makeText(requireContext(),"your information updated successfully",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(requireContext(),"Failed to update information!",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initCloudinary() {
        val config = mapOf(
            "cloud_name" to "dey9cixgd",
            "api_key" to "977563911513672",
            "api_secret" to "2vlkcLII2snvnmwnd9w8mJwAoVM"
        )

        cloudinary = Cloudinary(config)
    }
}
