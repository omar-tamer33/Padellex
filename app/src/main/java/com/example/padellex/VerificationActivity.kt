package com.example.padellex

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.padellex.databinding.ActivityVerificationBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class VerificationActivity : AppCompatActivity() {
    val auth = Firebase.auth
    lateinit var binding: ActivityVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sendEmailVerification()

        binding.backArrow.setOnClickListener {
            auth.signOut()
            navigateToLogin()
        }

        binding.checkAgainBtn.setOnClickListener {
            val user = auth.currentUser
            if (user != null){
                user.reload().addOnCompleteListener {
                    if (user.isEmailVerified){
                        navigateToLogin()
                    }else{
                        sendEmailVerification()
                    }
                }
            }
        }

    }

    private fun sendEmailVerification(){
        val user = auth.currentUser
        if (user != null){
            if (!user.isEmailVerified){
                user.sendEmailVerification().addOnSuccessListener {
                    Toast.makeText(this,"Email verification sent",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToLogin(){
        val intent = Intent(this , LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}