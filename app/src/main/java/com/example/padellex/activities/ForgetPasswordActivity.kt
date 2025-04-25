package com.example.padellex.activities

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.padellex.R
import com.example.padellex.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForgetPasswordActivity : AppCompatActivity() {
    @Inject lateinit var auth: FirebaseAuth
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

    private fun showAnimation(){
        binding.emailSentAnimation.isVisible = true
        binding.emailSentAnimation.repeatCount = 0
        binding.emailSentAnimation.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                binding.emailSentAnimation.isVisible = false
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
        binding.emailSentAnimation.playAnimation()
    }

    private fun sendForgetPasswordLink(email: String) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            showAnimation()
            Toast.makeText(this,"Reset Email sent",Toast.LENGTH_LONG).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this,"${e.message}",Toast.LENGTH_LONG).show()
        }
    }
}