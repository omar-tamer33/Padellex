package com.example.padellex.activities

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.padellex.Fragments.ChatsFragment
import com.example.padellex.Fragments.CourtsFragment
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
        changeFragment(HomeFragment::class.java,R.id.home)
        binding.bottomNavBar.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> changeFragment(HomeFragment::class.java,R.id.home)
                R.id.profile -> changeFragment(ProfileFragment::class.java,R.id.profile)
                R.id.booking -> changeFragment(CourtsFragment::class.java,R.id.booking)
                R.id.chats -> changeFragment(ChatsFragment::class.java,R.id.chats)
            }
            true
        }
    }

    fun changeFragment(fragment: Class<out Fragment>, @IdRes menuItemId: Int) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment,null).commit()
        binding.bottomNavBar.menu.findItem(menuItemId).isChecked = true
    }
}