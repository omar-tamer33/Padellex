package com.example.padellex.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.padellex.Fragments.BookingFragment
import com.example.padellex.Fragments.HomeFragment
import com.example.padellex.Fragments.ProfileFragment
import com.example.padellex.R
import com.example.padellex.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeFragment(HomeFragment())
        binding.bottomNavBar.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> changeFragment(HomeFragment())
                R.id.profile -> changeFragment(ProfileFragment())
                R.id.booking -> changeFragment(BookingFragment())
            }
            true
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit()
    }
}