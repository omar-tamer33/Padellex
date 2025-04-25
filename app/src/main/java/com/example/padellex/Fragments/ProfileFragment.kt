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
import com.example.padellex.activities.EditProfileActivity
import com.example.padellex.activities.UserBookingActivity
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

        viewModel.getUserInformation(auth.currentUser!!.uid)
        observeViewModel()

        binding.upcomingReservationBtn.setOnClickListener {
           navigateToUserReservation()
        }

        binding.editProfileBtn.setOnClickListener {
            navigateToEditProfile()
        }



        binding.signoutBtn.setOnClickListener {
            auth.signOut()
            navigateToLogin()
            activity?.finish()
        }


    }

    private fun navigateToLogin(){
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToUserReservation(){
        val intent = Intent(requireContext(), UserBookingActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToEditProfile(){
        val intent = Intent(requireContext(),EditProfileActivity::class.java)
        startActivity(intent)
    }


    private fun observeViewModel(){
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
                val imageUrl = userInfo.imageUrl

                binding.userEmailTv.text = auth.currentUser!!.email.toString()
                binding.userNameTv.text = "$firstName $lastName"
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
