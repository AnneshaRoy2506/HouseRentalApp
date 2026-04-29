package com.example.houserentalapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class OwnerHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_home)

        val btnBack = findViewById<ImageButton>(R.id.btnBackOwnerHome)
        val navProfile = findViewById<LinearLayout>(R.id.nav_owner_profile)

        btnBack.setOnClickListener {
            finish()
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, OwnerProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
