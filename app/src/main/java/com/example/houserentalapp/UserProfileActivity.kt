package com.example.houserentalapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserProfileActivity : AppCompatActivity() {

    private var isEditMode = false
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var userId: String
    private var selectedImageUri: Uri? = null

    private lateinit var ivUserProfileImage: ImageView
    private lateinit var btnEditToggle: MaterialButton
    private lateinit var btnSaveProfile: Button
    private lateinit var fabChangePic: FloatingActionButton
    
    private lateinit var etName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etBio: TextInputEditText

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            ivUserProfileImage.setImageURI(it)
            ivUserProfileImage.colorFilter = null
            selectedImageUri = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")
        storage = FirebaseStorage.getInstance().getReference("ProfilePictures")
        userId = auth.currentUser?.uid ?: ""

        val btnBack = findViewById<ImageButton>(R.id.btnBackUserProfile)
        val navHome = findViewById<LinearLayout>(R.id.nav_user_profile_to_home)
        
        ivUserProfileImage = findViewById(R.id.ivUserProfileImage)
        btnEditToggle = findViewById(R.id.btnEditUserToggle)
        btnSaveProfile = findViewById(R.id.btnSaveUserProfile)
        fabChangePic = findViewById<FloatingActionButton>(R.id.fabChangeUserPic)

        etName = findViewById(R.id.etUserProfileName)
        etPhone = findViewById(R.id.etUserProfilePhone)
        etAddress = findViewById(R.id.etUserProfileAddress)
        etBio = findViewById(R.id.etUserProfileBio)

        val fields = listOf(etName, etPhone, etAddress, etBio)

        // Load Profile Data immediately
        loadProfileData()

        btnBack.setOnClickListener {
            finish()
        }

        navHome.setOnClickListener {
            val intent = Intent(this, UserHomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        btnEditToggle.setOnClickListener {
            isEditMode = !isEditMode
            if (isEditMode) {
                btnEditToggle.text = getString(R.string.cancel)
                btnSaveProfile.visibility = View.VISIBLE
                fabChangePic.visibility = View.VISIBLE
                enableFields(fields, true)
            } else {
                btnEditToggle.text = getString(R.string.edit)
                btnSaveProfile.visibility = View.GONE
                fabChangePic.visibility = View.GONE
                enableFields(fields, false)
                loadProfileData() // Reload original data on cancel
            }
        }

        fabChangePic.setOnClickListener {
            pickImage.launch("image/*")
        }

        btnSaveProfile.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val bio = etBio.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = getString(R.string.name_required)
                return@setOnClickListener
            }

            if (selectedImageUri != null) {
                uploadImageAndSaveData(name, phone, address, bio)
            } else {
                saveProfileData(name, phone, address, bio, null)
            }
        }
    }

    private fun uploadImageAndSaveData(name: String, phone: String, address: String, bio: String) {
        val fileRef = storage.child("$userId.jpg")
        fileRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    saveProfileData(name, phone, address, bio, uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Image Upload Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfileData(name: String, phone: String, address: String, bio: String, imageUrl: String?) {
        val userUpdate = mutableMapOf<String, Any>(
            "name" to name,
            "phone" to phone,
            "address" to address,
            "bio" to bio
        )

        if (imageUrl != null) {
            userUpdate["profileImageUrl"] = imageUrl
        }

        if (userId.isNotEmpty()) {
            database.child(userId).updateChildren(userUpdate)
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.profile_updated_success), Toast.LENGTH_SHORT).show()
                    isEditMode = false
                    btnEditToggle.text = getString(R.string.edit)
                    btnSaveProfile.visibility = View.GONE
                    fabChangePic.visibility = View.GONE
                    enableFields(listOf(etName, etPhone, etAddress, etBio), false)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "${getString(R.string.failed_to_update)}: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadProfileData() {
        if (userId.isEmpty()) return

        database.child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                etName.setText(snapshot.child("name").value?.toString() ?: "")
                etPhone.setText(snapshot.child("phone").value?.toString() ?: "")
                etAddress.setText(snapshot.child("address").value?.toString() ?: "")
                etBio.setText(snapshot.child("bio").value?.toString() ?: "")
                
                val imageUrl = snapshot.child("profileImageUrl").value?.toString()
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_person)
                        .into(ivUserProfileImage)
                    ivUserProfileImage.colorFilter = null
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.error_loading_profile), Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableFields(fields: List<TextInputEditText>, enabled: Boolean) {
        for (field in fields) {
            field.isEnabled = enabled
        }
    }
}
