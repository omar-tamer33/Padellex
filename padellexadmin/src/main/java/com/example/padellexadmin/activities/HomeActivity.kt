package com.example.padellexadmin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.padellexadmin.R
import com.example.padellexadmin.databinding.ActivityHomeBinding
import com.example.padellexadmin.fragments.BookingFragment
import com.example.padellexadmin.fragments.BottomSheetFragment
import com.example.padellexadmin.fragments.CourtsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

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

    }

    private fun changeFragment(fragment: Class<out Fragment>){
        supportFragmentManager.beginTransaction().replace(R.id.fragmentLayout,fragment,null).commit()
    }

}