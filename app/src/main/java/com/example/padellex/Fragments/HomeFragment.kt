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
import com.example.padellex.R
import com.example.padellex.activities.ChatbotActivity
import com.example.padellex.activities.HomeActivity
import com.example.padellex.activities.UserBookingActivity
import com.example.padellex.databinding.FragmentHomeBinding
import com.example.padellex.viewModels.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {
    val viewModel: HomeViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth
    lateinit var binding: FragmentHomeBinding
    private val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_VIDEO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            viewModel.uploadVideo(inputStream,auth.currentUser!!.uid)
        } else {
            Toast.makeText(requireContext(),"Select video to add", Toast.LENGTH_LONG).show()
        }
    }
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                updateVideo()
            } else {
                showRationalDialog()
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserDetails(auth.currentUser!!.uid)
        observeViewModel()

        binding.reviewSkillsTv.setOnClickListener {
            checkPermissionGranted(storagePermission)
        }

        binding.chatBotBtn.setOnClickListener {
            val intent = Intent(requireContext(),ChatbotActivity::class.java)
            startActivity(intent)
        }

        binding.myBookingCard.setOnClickListener {
            val intent = Intent(requireContext(),UserBookingActivity::class.java)
            startActivity(intent)
        }

        binding.userChatCard.setOnClickListener {
            goToChats()
        }

        binding.bookingCard.setOnClickListener {
            goToCourts()
        }
    }

    private fun updateVideo() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
    }

    private fun checkPermissionGranted(permission : String){
        when{
            ContextCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED -> {
                updateVideo()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),permission) -> {
                showRationalDialog()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showRationalDialog() {
        dialog(title = "Why we need this permission ?" , message = "We need this permission to upload video" , positiveBtnText = "yes, i understand", onPositiveClick = {
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

    private fun observeViewModel(){
        viewModel.userDetails.observe(viewLifecycleOwner){userInfo->
            userInfo?.let {
                val firstName = userInfo.firstName
                val lastName = userInfo.lastName
                val imageUrl = userInfo.imageUrl

                binding.userNameTv.text = "$firstName $lastName"
                binding.playStyleTv.text = userInfo.playStyle
                binding.speedTv.text = userInfo.speed
                binding.powerTv.text = userInfo.power
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

        viewModel.playerRate.observe(viewLifecycleOwner){
            binding.playStyleTv.text = it?.get("playStyle")
            binding.speedTv.text = it?.get("playerSpeed")
            binding.powerTv.text = it?.get("shootSpeed")
        }
    }

    private fun goToChats() {
        (requireActivity() as? HomeActivity)
            ?.changeFragment(ChatsFragment::class.java, R.id.chats)
    }

    private fun goToCourts() {
        (requireActivity() as? HomeActivity)
            ?.changeFragment(CourtsFragment::class.java, R.id.booking)
    }
}