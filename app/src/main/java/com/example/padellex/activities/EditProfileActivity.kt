package com.example.padellex.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.padellex.Fragments.CustomAlertDialog
import com.example.padellex.R
import com.example.padellex.databinding.ActivityEditProfileBinding
import com.example.padellex.viewModels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {
    @Inject
    lateinit var auth : FirebaseAuth
    val viewModel : ProfileViewModel by viewModels()
    lateinit var binding : ActivityEditProfileBinding
    private val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val inputStream = this.contentResolver.openInputStream(uri)
            viewModel.uploadImage(inputStream,auth.currentUser!!.uid)
        } else {
            Toast.makeText(this,"Select image to add",Toast.LENGTH_LONG).show()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getUserInformation(auth.currentUser!!.uid)
        observeViewModel()

            binding.saveBtn.setOnClickListener {
                checkEditText()
            }

        binding.deleteBtn.setOnClickListener {
           warningAlertDialog()
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
        val alertDialog = AlertDialog.Builder(this)
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
            ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED -> {
                updateProfileImage()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this,permission) -> {
                showRationalDialog()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun warningAlertDialog(){
        CustomAlertDialog(message = "Your are about to delete your Account!"){
            viewModel.deleteAccount(auth.currentUser!!.uid)
            auth.signOut()
            val intent = Intent(
                this@EditProfileActivity, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }.show(supportFragmentManager,"warningDialog")
    }

    private fun checkEditText(){
        val password = binding.passwordEt.text.toString()
        val confirmPassword = binding.confirmPasswordEt.text.toString()
        val phone = binding.phoneEt.text.toString()
        val userId = auth.currentUser!!.uid
        if (password.isNotBlank() && confirmPassword.isNotBlank() && phone.isNotBlank()){
            updatePassword(password,confirmPassword)
            viewModel.updateUserInformation(userId,phone)
        }else{
            if (password.isBlank()) binding.passwordLayout.error = getString(R.string.empty_edittext)
            if (confirmPassword.isBlank()) binding.confirmPasswordLayout.error = getString(R.string.empty_edittext)
            if (phone.isBlank()) binding.phoneLayout.error = getString(R.string.empty_edittext)
        }
    }

    private fun updatePassword(password : String , confirmPassword : String){
        if (password.length >= 6 ) {
            if (password == confirmPassword) {
                viewModel.updatePassword(password)
            } else {
                binding.confirmPasswordLayout.error = getString(R.string.password_doesn_t_match)
            }
        }else{
            binding.passwordLayout.error = getString(R.string.password_error)
        }
    }

    private fun observeViewModel(){
        viewModel.success.observe(this){success->
            if (success){
                binding.phoneLayout.error = null
                Toast.makeText(this,"your phone updated successfully", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"Failed to update your phone!", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isImageAdded.observe(this){imageAdded->
            if (imageAdded) {
                Toast.makeText(this,"Image added successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"Failed to upload image", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.passwordSuccess.observe(this){isUpdated->
            if (isUpdated){
                binding.confirmPasswordLayout.error = null
                binding.passwordLayout.error = null
                Toast.makeText(this,"Password updated successfully", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"Failed to update Password", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.url.observe(this){imageUrl->
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.blank_profile_picture)
                    .into(binding.profileImage)
        }

        viewModel.userInfoItem.observe(this){userInfo->
            userInfo?.let {
                val firstName = userInfo.firstName
                val lastName = userInfo.lastName
                val phone = userInfo.phone
                val imageUrl = userInfo.imageUrl

                binding.userEmailTv.text = auth.currentUser!!.email.toString()
                binding.userNameTv.text = "$firstName $lastName"
                binding.phoneEt.setText(phone)
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.blank_profile_picture)
                        .into(binding.profileImage)
            } ?: run {
                Toast.makeText(
                    this,
                    "Failed to retrieve user info",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}