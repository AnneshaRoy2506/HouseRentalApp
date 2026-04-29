package com.example.houserentalapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class Home2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home2)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val rgRoles = findViewById<RadioGroup>(R.id.rgRoles)

        // GO BACK logic: explicit intent to MainActivity
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // CONTINUE logic
        btnContinue.setOnClickListener {
            val selectedId = rgRoles.checkedRadioButtonId

            if (selectedId == -1) {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show()
            } else {
                val radioButton = findViewById<RadioButton>(selectedId)
                val role = radioButton.text.toString()

                // Use ignoreCase = true because strings might be "ADMIN" or "Admin"
                if (role.equals("ADMIN", ignoreCase = true)) {
                    // Go to Admin Login Screen
                    val intent = Intent(this, AdminLoginActivity::class.java)
                    startActivity(intent)
                } else if (role.equals("OWNER", ignoreCase = true)) {
                    // Go to Owner Login Screen
                    val intent = Intent(this, OwnerSignInActivity::class.java)
                    startActivity(intent)
                } else if (role.equals("USER", ignoreCase = true)) {
                    // Go to User Login Screen
                    val intent = Intent(this, UserSignInActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}