package com.example.padellexadmin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.padellexadmin.Adapters.CourtAdapter
import com.example.padellexadmin.R
import com.example.padellexadmin.Repositories.CourtsRepository
import com.example.padellexadmin.databinding.ActivityHomeBinding
import com.example.padellexadmin.fragments.BookingFragment
import com.example.padellexadmin.fragments.BottomSheetFragment
import com.example.padellexadmin.fragments.CourtsFragment
import com.example.padellexadmin.model.CourtData
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeFragment(CourtsFragment())

        binding.bottomNavBar.setOnItemSelectedListener { id ->
            when(id.itemId){
                R.id.home -> changeFragment(CourtsFragment())
                R.id.booking -> changeFragment(BookingFragment())
            }
            return@setOnItemSelectedListener true
        }

        binding.addFab.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager,"BottomSheetFragment")
        }

    }

    fun changeFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragmentLayout,fragment).commit()
    }

}