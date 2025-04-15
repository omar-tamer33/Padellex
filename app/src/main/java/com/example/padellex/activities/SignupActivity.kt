package com.example.padellex.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.padellex.R
import com.example.padellex.Repositories.UserRepository
import com.example.padellex.databinding.ActivitySignupBinding
import com.example.padellex.model.UserInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class SignupActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    lateinit var binding: ActivitySignupBinding
    val databaseReference = FirebaseDatabase.getInstance()
    val usersDao = UserRepository(databaseReference)
    lateinit var email : String
    lateinit var password : String
    lateinit var firstName : String
    lateinit var lastName : String
    lateinit var phone : String
    var user : FirebaseUser? = null
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
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.createUserWithEmailAndPassword(email, password).await()
                        user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName("$firstName $lastName")
                            .build()
                        user?.updateProfile(profileUpdates)
                        withContext(Dispatchers.Main) {
                            if (user != null) {
                                saveUserInfoInDatabase(user!!.uid, firstName, lastName, phone)
                                navigateToVerification()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SignupActivity,"${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }

                }
            }else{
                binding.passwordLayout.error = getString(R.string.empty_edittext)
                binding.emailLayout.error = getString(R.string.empty_edittext)
                binding.firstNameLayout.error = getString(R.string.empty_edittext)
                binding.lastNameLayout.error = getString(R.string.empty_edittext)
                binding.phoneLayout.error = getString(R.string.empty_edittext)
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

    private fun saveUserInfoInDatabase(id : String? , firstName : String,lastName : String , phone : String){
        val userInfo = UserInfo(id = id, firstName = firstName, lastName = lastName, phone =phone)
       usersDao.addUser(userInfo){ success ->
           if (success){
               Toast.makeText(this,"Register completed",Toast.LENGTH_LONG).show()
           }else{
               Toast.makeText(this,"Register failed",Toast.LENGTH_LONG).show()

           }
       }
    }
}