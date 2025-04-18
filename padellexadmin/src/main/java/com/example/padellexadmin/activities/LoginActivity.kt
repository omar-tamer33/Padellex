package com.example.padellexadmin.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.padellexadmin.R
import com.example.padellexadmin.databinding.ActivityLoginBinding
import com.example.padellexadmin.viewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel : LoginViewModel by viewModels()
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()


        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            if (email.isNotBlank() && password.isNotBlank()) {
                viewModel.userLoginWithEmailAndPassword(email,password)
            }else{
                if (password.isBlank()) binding.passwordLayout.error = getString(R.string.empty_edittext)
                if (email.isBlank()) binding.emailLayout.error = getString(R.string.empty_edittext)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.checkUserLoginState()
    }

    private fun observeViewModel(){
        viewModel.isUserLoggedIn.observe(this){userLoggedIn->
            if (userLoggedIn){
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        viewModel.success.observe(this){success->
            if (success){
                binding.emailLayout.error = null
                binding.passwordLayout.error = null
            }else{
                binding.emailLayout.error = getString(R.string.error)
                binding.passwordLayout.error = getString(R.string.error)
            }
        }
    }


}