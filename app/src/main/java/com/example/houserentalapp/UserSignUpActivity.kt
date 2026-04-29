package com.example.houserentalapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserSignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_sign_up)

        auth = FirebaseAuth.getInstance()

        val etName = findViewById<EditText>(R.id.etUserSignUpName)
        val etEmail = findViewById<EditText>(R.id.etUserSignUpEmail)
        val etPassword = findViewById<EditText>(R.id.etUserSignUpPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etUserSignUpConfirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnUserSignUp)
        val tvSignIn = findViewById<TextView>(R.id.tvUserSignIn)
        val btnBack = findViewById<ImageButton>(R.id.btnBackUserSignUp)

        btnBack.setOnClickListener { finish() }

        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase User Registration
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    val userId = user.uid
                                    // Save role as "User" in Realtime Database
                                    val userMap = mapOf(
                                        "name" to name,
                                        "email" to email,
                                        "role" to "User"
                                    )
                                    FirebaseDatabase.getInstance().getReference("Users")
                                        .child(userId)
                                        .setValue(userMap)
                                        .addOnCompleteListener { dbTask ->
                                            if (dbTask.isSuccessful) {
                                                Toast.makeText(this, "Account Created! Verification email sent.", Toast.LENGTH_LONG).show()
                                                auth.signOut() // Sign out until verified
                                                finish()
                                            }
                                        }
                                }
                            }
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        tvSignIn.setOnClickListener { finish() }
    }
}