package com.example.padellexadmin.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.padellexadmin.R
import com.example.padellexadmin.databinding.ActivityHomeBinding
import com.example.padellexadmin.fragments.BookingFragment
import com.example.padellexadmin.fragments.BottomSheetFragment
import com.example.padellexadmin.fragments.CourtsFragment
import com.example.padellexadmin.viewModels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val viewModel : HomeViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding
    val notificationPermission = Manifest.permission.POST_NOTIFICATIONS
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.bookingNotification(this)
            } else {
                showRationalDialog()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeFragment(CourtsFragment::class.java)

        binding.bottomNavBar.setOnItemSelectedListener { id ->
            when(id.itemId){
                R.id.home -> changeFragment(CourtsFragment::class.java)
                R.id.booking -> changeFragment(BookingFragment::class.java)
            }
            return@setOnItemSelectedListener true
        }

        binding.addFab.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager,"BottomSheetFragment")
        }

        checkPermissionGranted(notificationPermission)

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun showRationalDialog() {
        showDialog(title = "why we need this permission" , message = "we need this permission to send notifications" , positiveBtnText = "yes i understand" , negativeBtnText = "no i refuse" , onPositiveClick = {
            requestPermissionLauncher.launch(notificationPermission)
        })
    }

    private fun checkPermissionGranted(permission: String) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.bookingNotification(this)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, permission) -> {
                showRationalDialog()
            }
            else -> {
                requestPermissionLauncher.launch(
                    permission)
            }
        }
    }

    private fun showDialog(title: String, message: String, positiveBtnText: String ? = null, onPositiveClick: () -> Unit, negativeBtnText: String, onNegativeClick: (() -> Unit?)? = null) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveBtnText) { dialog, which ->
                onPositiveClick()
                dialog.dismiss()
            }
            .setNegativeButton(negativeBtnText) { dialog, which ->
                if (onNegativeClick != null) {
                    onNegativeClick()
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun changeFragment(fragment: Class<out Fragment>){
        supportFragmentManager.beginTransaction().replace(R.id.fragmentLayout,fragment,null).commit()
    }

}