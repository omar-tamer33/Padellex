package com.example.padellex.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.padellex.databinding.ActivityVerificationBinding
import com.example.padellex.viewModels.VerificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationActivity : AppCompatActivity() {
    val viewModel : VerificationViewModel by viewModels()
    lateinit var binding: ActivityVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()

        viewModel.sendEmailVerification()

        binding.backArrow.setOnClickListener {
            viewModel.signOut()
            navigateToLogin()
        }

        binding.checkAgainBtn.setOnClickListener {
           viewModel.checkIsUserVerified()
        }

    }



    private fun navigateToLogin(){
        val intent = Intent(this , LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun observeViewModel(){
        viewModel.message.observe(this){message->
            Toast.makeText(this,message, Toast.LENGTH_LONG).show()
        }

        viewModel.isUserVerified.observe(this){isUserVerified->
            if (isUserVerified){
                navigateToLogin()
            }else{
                viewModel.sendEmailVerification()
            }
        }
    }

}