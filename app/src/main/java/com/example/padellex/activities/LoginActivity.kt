package com.example.padellex.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.padellex.R
import com.example.padellex.databinding.ActivityLoginBinding
import com.example.padellex.viewModels.LoginViewModel
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
            userLoginWithEmailAndPassword()
        }

        binding.signupTv.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.forgetPasswordTv.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.checkUserLoginState()
    }

    private fun navigateToHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToVerification(){
        val intent = Intent(this, VerificationActivity::class.java)
        startActivity(intent)
    }

    private fun userLoginWithEmailAndPassword(){
        val email = binding.emailEt.text.toString()
        val password = binding.passwordEt.text.toString()
        if (email.isNotBlank() && password.isNotBlank()){
           viewModel.userLoginWithEmailAndPassword(email,password)
        }else{
            if (password.isBlank()) binding.passwordLayout.error = getString(R.string.empty_edittext)
            if (email.isBlank()) binding.emailLayout.error = getString(R.string.empty_edittext)
        }
    }

    private fun showLoadingAnimation(){
        binding.loadingAnimation.isVisible = true
    }

    private fun observeViewModel(){
        viewModel.success.observe(this){success->
            if (success){
                binding.emailLayout.error = null
                binding.passwordLayout.error = null
            }else {
                binding.emailLayout.error = getString(R.string.error)
                binding.passwordLayout.error = getString(R.string.error)
            }
        }

        viewModel.isUserVerified.observe(this){userVerified ->
            if (userVerified){
                showLoadingAnimation()
                navigateToHome()
            }else{
                navigateToVerification()
            }
        }
    }
}