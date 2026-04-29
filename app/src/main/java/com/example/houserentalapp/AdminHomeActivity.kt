package com.example.houserentalapp

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class AdminHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        val btnBackHome = findViewById<ImageButton>(R.id.btnBackHome)
        btnBackHome.setOnClickListener {
            finish()
        }
    }
}