package com.example.houserentalapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class UserForgotPass2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_forgot_pass2)

        val btnBack = findViewById<ImageButton>(R.id.btnBackUserForgot2)
        val btnBackToSignIn = findViewById<Button>(R.id.btnUserBackToSignIn)

        btnBack.setOnClickListener { finish() }

        btnBackToSignIn.setOnClickListener {
            val intent = Intent(this, UserSignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}