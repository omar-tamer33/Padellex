package com.example.padellexadmin.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.padellexadmin.R
import com.example.padellexadmin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    @Inject lateinit var auth : FirebaseAuth
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.loginBtn.setOnClickListener {
            userLoginWithEmailAndPassword()
        }

    }

    override fun onStart() {
        super.onStart()
        checkUserLoginState()
    }

    private fun checkUserLoginState() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun userLoginWithEmailAndPassword(){
        val email = binding.emailEt.text.toString()
        val password = binding.passwordEt.text.toString()
        if (email.isNotBlank() && password.isNotBlank()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email,password).await()
                    checkUserLoginState()
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        binding.emailLayout.error = getString(R.string.error)
                        binding.passwordLayout.error = getString(R.string.error)
                    }
                }
            }
        }else{
            binding.passwordLayout.error = getString(R.string.empty_edittext)
            binding.emailLayout.error = getString(R.string.empty_edittext)
        }
    }
}