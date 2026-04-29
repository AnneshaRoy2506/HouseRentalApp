package com.example.houserentalapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class UserHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        val btnBack = findViewById<ImageButton>(R.id.btnBackUserHome)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)

        btnBack.setOnClickListener {
            finish()
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }
}