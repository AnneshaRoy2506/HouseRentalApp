package com.example.houserentalapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OwnerForgotPass2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_forgot_pass2)

        val btnBack = findViewById<ImageButton>(R.id.btnBackForgot2)
        val btnBackToSignIn = findViewById<Button>(R.id.btnBackToSignIn)

        btnBack.setOnClickListener { finish() }

        // Logic for the new professional flow
        btnBackToSignIn.setOnClickListener {
            val intent = Intent(this, OwnerSignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}