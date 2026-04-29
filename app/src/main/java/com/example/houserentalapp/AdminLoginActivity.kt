package com.example.houserentalapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AdminLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        val nameField = findViewById<EditText>(R.id.etAdminName)
        val passField = findViewById<EditText>(R.id.etAdminPassword)
        val loginBtn = findViewById<Button>(R.id.btnAdminLogin)
        val backBtn = findViewById<ImageButton>(R.id.btnBackLogin)

        backBtn.setOnClickListener { finish() }

        loginBtn.setOnClickListener {
            val name = nameField.text.toString()
            val pass = passField.text.toString()

            // FIXED CREDENTIALS CHECK
            if (name == "ANNESHA_ROY" && pass == "012345") {
                val intent = Intent(this, AdminHomeActivity::class.java)
                startActivity(intent)
                finish() // Close login screen so user can't go back to it
            } else {
                Toast.makeText(this, "Wrong Name or Password!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}