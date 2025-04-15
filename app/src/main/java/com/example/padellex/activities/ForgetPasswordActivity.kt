package com.example.padellex.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.padellex.R
import com.example.padellex.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.sendBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            if (email.isNotBlank()) {
                sendForgetPasswordLink(email)
            }else{
                binding.emailLayout.error = getString(R.string.empty_edittext)
            }
        }
    }

    private fun sendForgetPasswordLink(email: String) {
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            Toast.makeText(this,"Reset Email sent",Toast.LENGTH_LONG).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this,"${e.message}",Toast.LENGTH_LONG).show()
        }
    }
}