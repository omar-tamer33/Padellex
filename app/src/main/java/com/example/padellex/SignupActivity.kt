package com.example.padellex

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.padellex.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class SignupActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.signupBtn.setOnClickListener {
            userRegister()
        }

        binding.loginTv.setOnClickListener{
            navigateToLogin()
        }

    }

    private fun userRegister() {
        val email = binding.emailEt.text.toString()
        val password = binding.passwordEt.text.toString()
        val firstName = binding.firstNameEt.text.toString()
        val lastName = binding.lastNameEt.text.toString()
        if (password.length < 6) {
            binding.passwordLayout.error = getString(R.string.password_error)
        } else {
            binding.passwordLayout.error = null
            if (email.isNotBlank() && password.isNotBlank() && firstName.isNotBlank() && lastName.isNotBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.createUserWithEmailAndPassword(email, password).await()
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName("$firstName $lastName")
                            .build()
                        user?.updateProfile(profileUpdates)
                        navigateToLogin()
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SignupActivity, e.message, Toast.LENGTH_LONG).show()
                        }
                    }

                }
            }else{
                binding.passwordLayout.error = getString(R.string.empty_edittext)
                binding.emailLayout.error = getString(R.string.empty_edittext)
                binding.firstNameLayout.error = getString(R.string.empty_edittext)
                binding.lastNameLayout.error = getString(R.string.empty_edittext)
            }
        }
    }

    private fun navigateToLogin(){
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}