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

class OwnerProfileActivity : AppCompatActivity() {

    private var isEditMode = false
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var userId: String
    private var selectedImageUri: Uri? = null

    private lateinit var ivProfileImage: ImageView
    private lateinit var btnEditToggle: MaterialButton
    private lateinit var btnSaveProfile: Button
    private lateinit var fabChangePic: FloatingActionButton
    
    private lateinit var etName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etProfession: TextInputEditText
    private lateinit var etBio: TextInputEditText

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            ivProfileImage.setImageURI(it)
            ivProfileImage.colorFilter = null
            selectedImageUri = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")
        storage = FirebaseStorage.getInstance().getReference("ProfilePictures")
        userId = auth.currentUser?.uid ?: ""

        val btnBack = findViewById<ImageButton>(R.id.btnBackProfile)
        val navHome = findViewById<LinearLayout>(R.id.nav_profile_to_home)
        
        ivProfileImage = findViewById(R.id.ivProfileImage)
        btnEditToggle = findViewById(R.id.btnEditToggle)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        fabChangePic = findViewById(R.id.fabChangePic)

        etName = findViewById(R.id.etProfileName)
        etPhone = findViewById(R.id.etProfilePhone)
        etAddress = findViewById(R.id.etProfileAddress)
        etProfession = findViewById(R.id.etProfileProfession)
        etBio = findViewById(R.id.etProfileBio)

        val fields = listOf(etName, etPhone, etAddress, etProfession, etBio)

        loadProfileData()

        btnBack.setOnClickListener { finish() }

        navHome.setOnClickListener {
            val intent = Intent(this, OwnerHomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        btnEditToggle.setOnClickListener {
            isEditMode = !isEditMode
            toggleEditMode(fields)
        }

        fabChangePic.setOnClickListener {
            pickImage.launch("image/*")
        }

        btnSaveProfile.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val profession = etProfession.text.toString().trim()
            val bio = etBio.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = getString(R.string.name_required)
                return@setOnClickListener
            }

            if (selectedImageUri != null) {
                uploadImageAndSaveData(name, phone, address, profession, bio)
            } else {
                saveProfileData(name, phone, address, profession, bio, null)
            }
        }
    }

    private fun toggleEditMode(fields: List<TextInputEditText>) {
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
            loadProfileData()
        }
    }

    private fun uploadImageAndSaveData(name: String, phone: String, address: String, profession: String, bio: String) {
        val fileRef = storage.child("$userId.jpg")
        fileRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    saveProfileData(name, phone, address, profession, bio, uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Image Upload Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfileData(name: String, phone: String, address: String, profession: String, bio: String, imageUrl: String?) {
        val ownerUpdate = mutableMapOf<String, Any>(
            "name" to name,
            "phone" to phone,
            "address" to address,
            "profession" to profession,
            "bio" to bio
        )
        
        if (imageUrl != null) {
            ownerUpdate["profileImageUrl"] = imageUrl
        }

        if (userId.isNotEmpty()) {
            database.child(userId).updateChildren(ownerUpdate)
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.profile_updated_success), Toast.LENGTH_SHORT).show()
                    isEditMode = false
                    btnEditToggle.text = getString(R.string.edit)
                    btnSaveProfile.visibility = View.GONE
                    fabChangePic.visibility = View.GONE
                    enableFields(listOf(etName, etPhone, etAddress, etProfession, etBio), false)
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
                etProfession.setText(snapshot.child("profession").value?.toString() ?: "")
                etBio.setText(snapshot.child("bio").value?.toString() ?: "")
                
                val imageUrl = snapshot.child("profileImageUrl").value?.toString()
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_person)
                        .into(ivProfileImage)
                    ivProfileImage.colorFilter = null
                }
            }
        }
    }

    private fun enableFields(fields: List<TextInputEditText>, enabled: Boolean) {
        for (field in fields) field.isEnabled = enabled
    }
}
