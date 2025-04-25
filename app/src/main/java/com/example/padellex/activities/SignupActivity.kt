package com.example.padellex.activities

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.padellex.R
import com.example.padellex.databinding.ActivitySignupBinding
import com.example.padellex.viewModels.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {
    private val viewModel : SignupViewModel by viewModels()
    lateinit var binding: ActivitySignupBinding
    lateinit var email : String
    lateinit var password : String
    lateinit var firstName : String
    lateinit var lastName : String
    lateinit var phone : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()

        binding.signupBtn.setOnClickListener {
            userRegister()
        }

        binding.loginTv.setOnClickListener{
            navigateToLogin()
        }

    }

    private fun userRegister() {
         email = binding.emailEt.text.toString()
         password = binding.passwordEt.text.toString()
         firstName = binding.firstNameEt.text.toString()
         lastName = binding.lastNameEt.text.toString()
         phone = binding.phoneEt.text.toString()
        if (password.length < 6) {
            binding.passwordLayout.error = getString(R.string.password_error)
        } else {
            binding.passwordLayout.error = null
            if (email.isNotBlank() && password.isNotBlank() && firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()) {
                viewModel.userRegister(
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone
                )
            }else{
                if (email.isBlank()) binding.emailLayout.error = getString(R.string.empty_edittext)
                if (password.isBlank()) binding.passwordLayout.error = getString(R.string.empty_edittext)
                if (firstName.isBlank()) binding.firstNameLayout.error = getString(R.string.empty_edittext)
                if (lastName.isBlank()) binding.lastNameLayout.error = getString(R.string.empty_edittext)
                if (phone.isBlank()) binding.phoneLayout.error = getString(R.string.empty_edittext)
            }
        }
    }

    private fun navigateToVerification() {
        val intent = Intent(this, VerificationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun showAnimation(){
        binding.createAccountAnimation.isVisible = true
        binding.createAccountAnimation.repeatCount = 0
        binding.createAccountAnimation.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                navigateToVerification()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
        binding.createAccountAnimation.playAnimation()
    }

    private fun observeViewModel(){
        viewModel.errorMessage.observe(this){error ->
            Toast.makeText(this,error,Toast.LENGTH_LONG).show()
        }

        viewModel.successMessage.observe(this){message ->
            Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        }

        viewModel.isUserRegistered.observe(this){ userRegistered ->
            if (userRegistered){
                showAnimation()
            }
        }
    }

}