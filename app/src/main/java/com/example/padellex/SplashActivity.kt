package com.example.padellex

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({navigateActivity()},3000)

    }

    private fun navigateActivity() {
        val intent : Intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}