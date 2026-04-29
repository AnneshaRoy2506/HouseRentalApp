package com.example.houserentalapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OwnerForgotPass3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_forgot_pass3)

        val btnBack = findViewById<ImageButton>(R.id.btnBackForgot3)
        val btnSave = findViewById<Button>(R.id.btnSavePassword)

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            // Logic to save new password
            Toast.makeText(this, "Password Saved Successfully!", Toast.LENGTH_SHORT).show()
            
            // Go back to Sign-in
            val intent = Intent(this, OwnerSignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}