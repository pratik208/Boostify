package com.codecrafters.boostifyapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class LoginAddUser : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private lateinit var ptxt_firstname: EditText
    private lateinit var ptxt_lastname: EditText
    private lateinit var ptxt_email: EditText
    private lateinit var ptxtpassword: EditText
    private lateinit var ptxt_cpassword: EditText
    private lateinit var ptxt_number: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var selectPhoto: CircleImageView
    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_add_user)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        ptxt_firstname = findViewById(R.id.ptxt_firstname)
        ptxt_lastname = findViewById(R.id.ptxt_lastname)
        ptxt_email = findViewById(R.id.ptxt_email)
        ptxtpassword = findViewById(R.id.ptxtpassword)
        ptxt_cpassword = findViewById(R.id.ptxt_cpassword)
        ptxt_number = findViewById(R.id.ptxt_number)
        progressBar = findViewById(R.id.progressbar)
        selectPhoto = findViewById(R.id.selectphoto)

        selectPhoto.setOnClickListener {
            // Use an Intent to open the image picker
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1)
        }

        val btn_createuser = findViewById<Button>(R.id.btn_createuser)
        btn_createuser.setOnClickListener {
            val firstName = ptxt_firstname.text.toString()
            val lastName = ptxt_lastname.text.toString()
            val email = ptxt_email.text.toString()
            val password = ptxtpassword.text.toString()
            val mobNumber = ptxt_number.text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (password == ptxt_cpassword.text.toString()) {
                    progressBar.visibility = View.VISIBLE

                    // Create a new user in Firebase Authentication
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // User is registered, now save user data to Firebase Realtime Database
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    val userRef = database.reference.child("users").child(userId)
                                    userRef.child("firstName").setValue(firstName)
                                    userRef.child("lastName").setValue(lastName)
                                    userRef.child("Number").setValue(mobNumber)

                                    // Upload profile photo to Firebase Storage (selectedPhotoUri should be set)
                                    if (selectedPhotoUri != null) {
                                        val profilePhotoRef = storageRef.child("profilePhotos").child(userId)
                                        profilePhotoRef.putFile(selectedPhotoUri!!)
                                    }

                                    // Redirect to a new activity after successful registration
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                }
                            } else {
                                // Handle registration failure (e.g., display an error message)
                                progressBar.visibility = View.GONE
                                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                            progressBar.visibility = View.GONE
                        }
                } else {
                    // Passwords don't match, display an error message
                    Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Fields are empty, display an error message
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            // Get the selected image's URI
            val imageUri = data?.data

            // Update the CircleImageView with the selected image using Glide
            if (imageUri != null) {
                Glide.with(this)
                    .load(imageUri)
                    .into(selectPhoto)
                selectedPhotoUri = imageUri
            }
        }
    }
}
