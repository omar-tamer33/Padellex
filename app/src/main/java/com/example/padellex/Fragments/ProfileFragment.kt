package com.example.padellex.Fragments

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.example.padellex.CourtItem
import com.example.padellex.Dao.UsersDao
import com.example.padellex.LoginActivity
import com.example.padellex.R
import com.example.padellex.UserInfo
import com.example.padellex.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class ProfileFragment : Fragment() {
    private val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    lateinit var binding: FragmentProfileBinding
    val auth = Firebase.auth
    val user = auth.currentUser
    val databaseReference = FirebaseDatabase.getInstance()
    val usersDao = UsersDao(databaseReference)
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
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
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
            val intent = Intent(requireContext(),LoginActivity::class.java)
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
        val profileUpdates = UserProfileChangeRequest.Builder().setPhotoUri(uri).build()
        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.profileImage.setImageURI(uri)
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
        val userId = user?.uid ?: return
        usersDao.getUser(userId){ userInfo ->
            userInfo?.let {
                val firstName = userInfo.firstName
                val lastName = userInfo.lastName
                val phone = userInfo.phone
                val imageUrl = userInfo.image

                binding.userEmailTv.text = user.email.toString()
                binding.userNameTv.text = "$firstName $lastName"
                binding.userPhoneEt.setText(phone)

                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.blank_profile_picture)
                    .into(binding.profileImage)
            } ?: run {
                Toast.makeText(requireContext(), "Failed to retrieve user info", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserInformation(){
            val phone = binding.userPhoneEt.text.toString()
            val userId = user?.uid ?: return
        usersDao.updateUserPhone(userId,phone){ success ->
            if (success){
                Toast.makeText(requireContext(),"your information updated successfully",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(requireContext(),"Failed to update information!",Toast.LENGTH_LONG).show()
            }
        }
    }
}
